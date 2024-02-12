package com.evolve.alpaca.importing.importDbf.turnover;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
@ToString
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

/*
DbfTurnover(MK=2002011, SD=RK, ND=     5, LP=  14, KO=4000301, KWWN=26.40, KWMA=0.00, WAL=0.00, DD=Thu Jan 17 00:00:00 CET 2002, TO=, TOR=, FP=, TP=, DV=, UZ=*, DAR=20031231, NIP=false, POZABIL=false, KONTRAKT=, MRK=, ZRODLO=, KAUCJA=)
DbfTurnover(MK=2002011, SD=RK, ND=     5, LP=  15, KO=101, KWWN=0.00, KWMA=3759.10, WAL=0.00, DD=Thu Jan 17 00:00:00 CET 2002, TO=, TOR=, FP=, TP=, DV=, UZ=*, DAR=20031230, NIP=false, POZABIL=false, KONTRAKT=, MRK=, ZRODLO=, KAUCJA=)
 */

    public static DbfTurnover of(Map<String, Object> rawData) {
        final MapWrapper data = new MapWrapper(rawData);

        return DbfTurnover.builder()
                .MK(data.get("MK"))
                .SD(data.get("SD"))
                .ND(data.get("ND"))
                .LP(data.get("LP"))
                .KO(data.get("KO"))
                .KWWN(data.get("KWWN"))
                .KWMA(data.get("KWMA"))
                .WAL(data.get("WAL"))
                .DD(data.get("DD"))
                .TO(data.get("TO"))
                .TOR(data.get("TOR"))
                .FP(data.get("FP"))
                .TP(data.get("TP"))
                .DV(data.get("DV"))
                .UZ(data.get("UZ"))
                .DAR(data.get("DAR"))
                .NIP(data.get("NIP"))
                .POZABIL(data.get("POZABIL"))
                .KONTRAKT(data.get("KONTRAKT"))
                .MRK(data.get("MRK"))
                .ZRODLO(data.get("ZRODLO"))
                .KAUCJA(data.get("KAUCJA"))
                .build();
    }


    @RequiredArgsConstructor
    public static class MapWrapper {
        private final Map<String, Object> map;

        public String get(String key) {
            return toString(map.get(key));
        }

        private static String toString(Object input) {
            return input == null ? "" : input.toString();
        }

    }

}
