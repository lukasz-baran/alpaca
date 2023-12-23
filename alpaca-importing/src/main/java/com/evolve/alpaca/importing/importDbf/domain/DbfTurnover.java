package com.evolve.alpaca.importing.importDbf.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Builder
@Getter
public class DbfTurnover {

    /*
        RRRRMMX - miesiąc księgowy
            X=”1” –zapis obrotowy
            X=„ ” -zapis bilansu otwarcia
            X=„0” -korekta BO
     */
    private String MK;

    private String SD; // symbol dokumentu księgowego (RB)
    private String ND; // numer dokumentu
    private String LP; // kolejny numer pozycji w ramach dokumentu
    private String KO; // symbol konta księgowego

    private String KWWN; // kwota strony WN (numeric)
    private String KWMA; // kwota strony MA (numeric)
    private String WAL; // wartość transakcji w walucie
    private String DD; // data dokumentu
    private String TO; // treść operacji (numer faktury)
    private String TOR; // treść operacji po skojarzeniu zapłaty z należnością

    private String FP; // forma zapłaty
    private String TP; // termin płatności
    private String DV; // data powstania obowiązku VAT
    private String UZ; // kod użytkownika, który dokonał zapisu dokumentu
    private String DAR; // data rozliczenia transakcji: 99999999 – transakcja nie rozliczona

    private String NIP; // wskaźnik, czy z dokumentem jest związany zapis w zbiorze nipy.dbf
    private String POZABIL; // znacznik zapisu pozabilans.

    private String KONTRAKT;
    private String MRK;
    private String ZRODLO;
    private String KAUCJA;

    public static DbfTurnover of(Map<String, Object> data) {
        return DbfTurnover.builder()
                .MK(data.get("MK").toString())
                .SD(data.get("SD").toString())
                .ND(data.get("ND").toString())
                .LP(data.get("LP").toString())
                .KO(data.get("KO").toString())
                .KWWN(data.get("KWWN").toString())
                .KWMA(data.get("KWMA").toString())
                .WAL(data.get("WAL").toString())
                .DD(data.get("DD").toString())
                .TO(data.get("TO").toString())
                .TOR(data.get("TOR").toString())
                .FP(data.get("FP").toString())
                .TP(data.get("TP").toString())
                .DV(data.get("DV").toString())
                .UZ(data.get("UZ").toString())
                .DAR(data.get("DAR").toString())
                .NIP(data.get("NIP").toString())
                .POZABIL(data.get("POZABIL").toString())
                .KONTRAKT(data.get("KONTRAKT").toString())
                .MRK(data.get("MRK").toString())
                .ZRODLO(data.get("ZRODLO").toString())
                .KAUCJA(data.get("KAUCJA").toString())
                .build();
    }

}
