package com.evolve.alpaca.turnover;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentType {

    POSTING_ORDER("PK"), // PK - polecenie księgowania
    CURRENT_ACCOUNT("RB"), // RB - albo raport bankowy
    CASH_ACCOUNT("RK"); // RK - rachunek kasowy

    private final String symbol;

}
