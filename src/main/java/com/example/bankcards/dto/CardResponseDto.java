package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponseDto {
    private Long id;
    private String maskedNumber;
    private String ownerName;
    private BigDecimal balance;
    private Status status;
    private LocalDate expirationDate;
}