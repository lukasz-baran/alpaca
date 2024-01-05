package com.evolve.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@RequiredArgsConstructor
@ToString
@Jacksonized
@Builder
@Getter
public class BankDetails {

    private final List<Owner> listaWlascicieli;

    @Jacksonized
    @Builder
    public record Owner(String numer, String nazwa, String symbol, String typ, String adresWWW, Address siedziba,
                        List<Branch> jednostki) {
    }

    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(String numer, String nazwa, String typ, String miejscowosc, String ulicaNrBudynku,
                          String kodPocztowy, String miejscowoscPoczta, String nrTelefonu1,
                          String kraj, String wojewodztwo, String powiat) {
    }

    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Branch(String numer, String nazwa, String typ, String miejscowosc, String ulicaNrBudynku,
                         String kodPocztowy, String miejscowoscPoczta, String nrTelefonu1,
                         String kraj, String wojewodztwo, String powiat, List<BillingNumer> numeryRozliczeniowe) {
    }

    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BillingNumer(String numer, String nazwa, List<BicNumber> numeryBic) {
    }

    @Jacksonized
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BicNumber(String nazwa, String numer) {
    }
}
