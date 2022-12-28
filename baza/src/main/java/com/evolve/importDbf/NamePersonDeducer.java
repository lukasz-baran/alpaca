package com.evolve.importDbf;

public class NamePersonDeducer {

    private final String nazwa1;
    private final String nazwa2;

    public NamePersonDeducer(DbfPerson dbfPerson) {
        this.nazwa1 = dbfPerson.getNAZ_ODB1().trim();
        this.nazwa2 = dbfPerson.getNAZ_ODB2().trim();
    }

    // TODO deduce and compare names from the fields


}
