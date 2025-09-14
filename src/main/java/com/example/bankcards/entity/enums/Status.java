package com.example.bankcards.entity.enums;

public enum Status {
    ACTIVE,
    BLOCKED,
    EXPIRED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
