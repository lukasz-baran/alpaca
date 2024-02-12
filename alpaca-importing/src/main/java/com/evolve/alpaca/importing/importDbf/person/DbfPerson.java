package com.evolve.alpaca.importing.importDbf.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class DbfPerson {
      @JsonIgnore
      private final Map<String, String> data;
      private String SYM_ODB;
      private String NAZ_ODB1;
      private String NAZ_ODB2;
      private String NAZ_ODB3;
      private String NAZ_ODB4;
      private String NAZ_ODB5;
      private String NAZ_ODB6;
      private String NAZ_ODB7;
      private String GRU_ODB;
      private String BANK0;
      private String BANK1;
      private String BANK2;
      private String BANK3;
      private String BANK4;
      private String KONTO_WNP; // if starts with "200" - member, "807" - dead
      private String KONTO_WNI;
      private String RO;
      private String CZAS_R; // IGNORE - always 0
      private Date DATA_ZAL;
      private Date DATA_AKT;
      private String NR_IDENT; // numer starej kartoteki + number nowej kartoteki, np. " 1 594  2362"
      private Boolean VAT;  // ignore - always False
      private String TEL0;
      private String TEL1;
      private String NIP_UE;
      private String SYM_KRAJ;
      private String WWW;  // ignore - never filled
      private String EMAIL;
      private String ZRODLO;
      private String INFO;
      private String PLATNIK; // ignore - always empty
      private String UZ;

      static Map<String, String> rawData(Map<String, Object> data) {
            if (data == null) {
                  return Map.of();
            }
            return data.entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey,
                            a -> nullToEmpty(a.getValue()), (prev, next) -> next, HashMap::new));
      }

      private static String nullToEmpty(Object object) {
            return object == null ? "" : object.toString();
      }

      public static DbfPerson of(Map<String, Object> data) {
         return DbfPerson.builder()
              .data(rawData(data))
              .SYM_ODB(data.get("SYM_ODB").toString())
              .NAZ_ODB1(data.get("NAZ_ODB1").toString())
              .NAZ_ODB2(data.get("NAZ_ODB2").toString())
              .NAZ_ODB3(data.get("NAZ_ODB3").toString())
              .NAZ_ODB4(data.get("NAZ_ODB4").toString())
              .NAZ_ODB5(data.get("NAZ_ODB5").toString())
              .NAZ_ODB6(data.get("NAZ_ODB6").toString())
              .NAZ_ODB7(data.get("NAZ_ODB7").toString())
              .GRU_ODB(data.get("GRU_ODB").toString())
              .BANK0(data.get("BANK0").toString())
              .BANK1(data.get("BANK1").toString())
              .BANK2(data.get("BANK2").toString())
              .BANK3(data.get("BANK3").toString())
              .BANK4(data.get("BANK4").toString())
              .RO(data.get("RO").toString())
              .KONTO_WNI(data.get("KONTO_WNI").toString())
              .KONTO_WNP(data.get("KONTO_WNP").toString())
              .DATA_ZAL((Date)data.get("DATA_ZAL"))
              .DATA_AKT((Date)data.get("DATA_AKT"))
              .NR_IDENT(data.get("NR_IDENT").toString())
              .TEL0(data.get("TEL0").toString())
              .TEL1(data.get("TEL1").toString())
              .NIP_UE(data.get("NIP_UE").toString())
              .SYM_KRAJ(data.get("SYM_KRAJ").toString())
              .ZRODLO(data.get("ZRODLO").toString())
              .EMAIL(data.get("EMAIL").toString())
              .INFO(data.get("INFO").toString())
              .UZ(data.get("UZ").toString())
              .build();
   }

}
