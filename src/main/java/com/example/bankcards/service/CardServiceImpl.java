package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.TransferNotAllowedException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.config.JwtService;
import com.example.bankcards.util.CardNumberMasker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberMasker cardNumberMasker;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    @Transactional
    public CardResponseDto createCard(CardRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = new Card();
        card.setCardNumber(request.getCardNumber());
        card.setOwnerName(request.getOwnerName());
        card.setExpirationDate(request.getExpirationDate());
        card.setBalance(request.getInitialBalance());
        card.setStatus(Status.ACTIVE);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponseDto> getUserCards(Pageable pageable) {
        String username = getCurrentUsername();
        return cardRepository.findAllByUserUsername(username, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponseDto> getAllCards(Pageable pageable) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Only admin can access all cards");
        }
        return cardRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponseDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));

        if (!isAdmin()) {
            if (!card.getUser().getUsername().equals(getCurrentUsername())) {
                throw new AccessDeniedException("You don't have access to this card");
            }
        }
        return convertToDto(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Only admin can delete cards");
        }
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void transferBetweenOwnCards(TransferRequestDto request) {
        String username = getCurrentUsername();

        // 1. Находим карты и сразу проверяем принадлежность
        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getFromCardId()));

        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new CardNotFoundException(request.getToCardId()));

        // 2. Проверяем принадлежность ОДНИМ запросом (через объекты)
        if (!fromCard.getUser().getUsername().equals(username)) {
            throw new TransferNotAllowedException("Source card does not belong to you");
        }

        if (!toCard.getUser().getUsername().equals(username)) {
            throw new TransferNotAllowedException("Target card does not belong to you");
        }

        // 3. Проверяем что это не перевод на ту же карту
        if (fromCard.getId().equals(toCard.getId())) {
            throw new TransferNotAllowedException("Cannot transfer to the same card");
        }

        // 4. Проверяем статус карт
        if (fromCard.getStatus() != Status.ACTIVE) {
            throw new TransferNotAllowedException("Source card is not active. Current status: " + fromCard.getStatus());
        }

        if (toCard.getStatus() != Status.ACTIVE) {
            throw new TransferNotAllowedException("Target card is not active. Current status: " + toCard.getStatus());
        }

        // 5. Проверяем достаточность средств
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds on card %s. Available: %s, Required: %s",
                            fromCard.getId(), fromCard.getBalance(), request.getAmount())
            );
        }

        // 6. Проверяем положительную сумму перевода
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferNotAllowedException("Transfer amount must be positive");
        }

        // 7. Выполняем перевод
        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        // 8. Сохраняем изменения
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Override
    @Transactional
    public CardResponseDto blockCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        if (!isAdmin() && !card.getUser().getUsername().equals(getCurrentUsername())) {
            throw new AccessDeniedException("You can only block your own cards");
        }

        card.setStatus(Status.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }

    @Override
    @Transactional
    public CardResponseDto activateCard(Long id) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Only admin can activate cards");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        card.setStatus(Status.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }

    private CardResponseDto convertToDto(Card card) {
        CardResponseDto dto = new CardResponseDto();
        dto.setId(card.getId());
        dto.setMaskedNumber(cardNumberMasker.maskCardNumber(card.getCardNumber()));
        dto.setOwnerName(card.getOwnerName());
        dto.setBalance(card.getBalance());
        dto.setStatus(card.getStatus());
        dto.setExpirationDate(card.getExpirationDate());
        return dto;
    }
}