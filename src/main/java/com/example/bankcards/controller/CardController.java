package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Bank cards management")
public class CardController {

    private final CardService cardService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new card", description = "This method creates a new card")
    public ResponseEntity<CardResponseDto> createCard(@RequestBody @Valid CardRequestDto request) {
        CardResponseDto card = cardService.createCard(request);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/my")
    @Operation(summary = "Get user's cards", description = "This method shows to user his own cards")
    public ResponseEntity<Page<CardResponseDto>> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<CardResponseDto> cards = cardService.getUserCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all cards", description = "This method shows to admin all the cards")

    public ResponseEntity<Page<CardResponseDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<CardResponseDto> cards = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID", description = "This method shows to admin any card by ID, but user can find by ID only his own card")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable Long id) {
        CardResponseDto card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete card", description = "This method deletes card from teh database")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Transfer", description = "This method transfers money between user's own cards")
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequestDto request) {
        cardService.transferBetweenOwnCards(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Block card", description = "This method clocks cards")
    public ResponseEntity<CardResponseDto> blockCard(@PathVariable Long id) {
        CardResponseDto card = cardService.blockCard(id);
        return ResponseEntity.ok(card);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate card", description = "This method activates cards")
    public ResponseEntity<CardResponseDto> activateCard(@PathVariable Long id) {
        CardResponseDto card = cardService.activateCard(id);
        return ResponseEntity.ok(card);
    }
}