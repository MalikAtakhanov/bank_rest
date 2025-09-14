package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.TransferRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

    CardResponseDto createCard(CardRequestDto request);

    Page<CardResponseDto> getUserCards(Pageable pageable);

    Page<CardResponseDto> getAllCards(Pageable pageable);

    CardResponseDto getCardById(Long id);

    void deleteCard(Long id);

    void transferBetweenOwnCards(TransferRequestDto request);

    CardResponseDto blockCard(Long id);

    CardResponseDto activateCard(Long id);
}