package com.evolve.alpaca.importing.importDbf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
@ToString(exclude = { "data", "WYJATEK", "UZ", "KONTOBO", "POZABIL", "WALUTA", "WALUTOWE", "BLOKADA",
        "DA", "WYNIKOWE", "SR" })
public class DbfAccount {
    @JsonIgnore
    private final Map<String, Object> data;

    private String KS;
    private String NA;
    private String SR; // probably ignore - always empty or .
    private String WYJATEK; // ignore - always empty
    private String ROZRA;
    private String DA; // ignore - always null
    private String UZ; // ignore - always empty
    private String NA1;
    private String WALUTOWE; // ignore - always false
    private String WALUTA; // ignore - always empty
    private String WYNIKOWE; // ignore - always false
    private String BLOKADA; // ignore - always false
    private String POZABIL; // ignore - always false
    private String KONTOBO; // ignore - always empty
    private String WSK;

    public static DbfAccount of(Map<String, Object> data) {
        return DbfAccount.builder()
                .data(data)
                .KS(data.get("KS").toString())
                .NA(data.get("NA").toString())
                .SR(data.get("SR").toString())
                .WYJATEK(data.get("WYJATEK").toString())
                .ROZRA(data.get("ROZRA").toString())
                .DA(Optional.ofNullable(data.get("DA")).map(Object::toString).orElse(null))
                .UZ(data.get("UZ").toString())
                .NA1(data.get("NA1").toString())
                .WALUTOWE(data.get("WALUTOWE").toString())
                .WALUTA(data.get("WALUTA").toString())
                .WYNIKOWE(data.get("WYNIKOWE").toString())
                .BLOKADA(data.get("BLOKADA").toString())
                .POZABIL(data.get("POZABIL").toString())
                .KONTOBO(data.get("KONTOBO").toString())
                .WSK(data.get("WSK").toString())
                .build();
    }

}
