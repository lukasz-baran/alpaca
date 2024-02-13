package com.evolve.alpaca.turnover;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Turnover {
/*
DbfTurnover(MK=2002011, SD=RK, ND=     5, LP=  14, KO=4000301, KWWN=26.40, KWMA=0.00, WAL=0.00, DD=Thu Jan 17 00:00:00 CET 2002, TO=, TOR=, FP=, TP=, DV=, UZ=*, DAR=20031231, NIP=false, POZABIL=false, KONTRAKT=, MRK=, ZRODLO=, KAUCJA=)
DbfTurnover(MK=2002011, SD=RK, ND=     5, LP=  15, KO=101, KWWN=0.00, KWMA=3759.10, WAL=0.00, DD=Thu Jan 17 00:00:00 CET 2002, TO=, TOR=, FP=, TP=, DV=, UZ=*, DAR=20031230, NIP=false, POZABIL=false, KONTRAKT=, MRK=, ZRODLO=, KAUCJA=)
 */

    private String month;
    private DocumentType documentType;

    private String documentNumber;
    private String lp;

    private String accountId;
    private String debit;
    private String credit;


}
