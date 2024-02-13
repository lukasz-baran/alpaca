package com.evolve.alpaca.turnover;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentType {

    POSTING_ORDER("PK"), // PK - polecenie księgowania
    CURRENT_ACCOUNT("RB"), // RB - albo raport bankowy
    CASH_ACCOUNT("RK"); // RK - rachunek kasowy

    private final String symbol;

    public static DocumentType fromString(String text) {
        for (DocumentType b : DocumentType.values()) {
            if (b.symbol.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new RuntimeException("unknown value " + text);
    }
}
