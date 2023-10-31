package com.evolve;

import com.evolve.alpaca.importing.importDbf.ImportAccountDbf;
import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.alpaca.importing.importDbf.ImportPersonDbf;
import com.evolve.alpaca.importing.importDoc.ImportAlphanumeric;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.alpaca.importing.importDoc.person.Person;
import com.evolve.alpaca.importing.importDbf.PersonsFactory;
import com.evolve.alpaca.utils.LogUtil;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static com.evolve.alpaca.importing.importDoc.ImportAlphanumeric.FILENAME_BY_ALPHA;
import static com.evolve.alpaca.importing.importDoc.ImportPeople.FILENAME_BY_NUMBERS;

@Slf4j
public class AppMain {
    public static final URL PERSONS_DBF_FILE = Resources.getResource("Z_B_KO.DBF");
    public static final URL ACCOUNTS_DBF_FILE = Resources.getResource("PLAN.DBF");

    @SneakyThrows
    public static void main(String[] args) {
        //new AppMain().imports();
        new AppMain().importAccounts();
    }

    public void importAccounts() {
        final List<DbfAccount> kontaDbf = new ImportAccountDbf()
                .performImport(ACCOUNTS_DBF_FILE.getPath())
                .getItems();
        for (DbfAccount dbfAccount : kontaDbf) {
            System.out.println(dbfAccount);
        }
//        System.out.println("ROZRA    " + kontaDbf.stream().map(DbfAccount::getROZRA)
//                .distinct().toList());
//        System.out.println("UZ       " + kontaDbf.stream().map(DbfAccount::getUZ)
//                .distinct().toList());
//        System.out.println("KONTOBO  " + kontaDbf.stream().map(DbfAccount::getKONTOBO)
//                .distinct().toList());
//        System.out.println("WYJATEK  " + kontaDbf.stream().map(DbfAccount::getWYJATEK)
//                .distinct().toList());
//        System.out.println("POZABIL  " + kontaDbf.stream().map(DbfAccount::getPOZABIL)
//                .distinct().toList());
//        System.out.println("WALUTA   " + kontaDbf.stream().map(DbfAccount::getWALUTA)
//                .distinct().toList());
//        System.out.println("WALUTOWE " + kontaDbf.stream().map(DbfAccount::getWALUTOWE)
//                .distinct().toList());
//        System.out.println("BLOKADA  " + kontaDbf.stream().map(DbfAccount::getBLOKADA)
//                .distinct().toList());
//        System.out.println("DA       " + kontaDbf.stream().map(DbfAccount::getDA)
//                .distinct().toList());
//        System.out.println("NA1      " + kontaDbf.stream().map(DbfAccount::getNA1)
//                .distinct().toList());
//        System.out.println("WYNIKOWE " + kontaDbf.stream().map(DbfAccount::getWYNIKOWE)
//                .distinct().toList());
//        System.out.println("SR       " + kontaDbf.stream().map(DbfAccount::getSR)
//                .distinct().toList());
    }

    @SneakyThrows
    public void imports() {
        final List<Person> people = new ImportPeople(true).processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric()
                .processFile();

        final List<DbfPerson> osobyDbf = new ImportPersonDbf()
                .performImport(PERSONS_DBF_FILE.getPath())
                .getItems();

        var numery = osobyDbf.stream().map(DbfPerson::getNR_IDENT).collect(Collectors.toList());
        System.out.println(numery);

        final List<com.evolve.domain.Person> persons = new PersonsFactory().from(osobyDbf);

        log.info("Wczytano " + people.size() + " z indeksu " + FILENAME_BY_NUMBERS);
        log.info("Wczytano " + grupyAlfabetyczne.getSize() + " z grup alfabetycznych " + FILENAME_BY_ALPHA);
        log.info("Wczytano " + osobyDbf.size());

        persons.forEach(person -> {
           verifyData(person, people, grupyAlfabetyczne);
        });
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValue(new File("osobyDbf.json"), osobyDbf);
//
//        List<com.evolve.domain.Person> osoby =new PersonsFactory().from(osobyDbf);

        //System.out.println(osoby);

    }


    void verifyData(com.evolve.domain.Person person, List<Person> persons, GrupyAlfabetyczne grupyAlfabetyczne) {
        final String id = person.getPersonId();
        persons.stream().filter(p -> id.equals(p.getPersonId())) //
                .findFirst() //
                .ifPresent(p -> {
                    if (person.getRegistryNumber() == null) {
                        log.error("Brak numeru kartoteki dla osoby: " + LogUtil.printJson(person));
                    } else {

                        final Integer numerKartoteki = Integer.parseInt(p.getNumerKartoteki().getId());

                        if (!numerKartoteki.equals(person.getRegistryNumber().getRegistryNum())) {
//                            log.error("numer kartoteki is wrong : " + p.getNumerKartoteki() + " " + person.getRegistryNumber().getRegistryNum());
//                            log.error(" dbf person" + LogUtil.printJson(person));
//                            log.error(" file person" + LogUtil.printJson(p));


                            log.error(" Numer kartoteki się nie zgadza: {} {} {} {} {}  -- {} {} {} {} {}",
                                    person.getPersonId(),
                                    person.getLastName(),
                                    person.getFirstName(),
                                    person.getRegistryNumber().getRegistryNum(),
                                    person.getRegistryNumber().getOldRegistryNum(),

                                    p.getPersonId(),
                                    p.getLastName(),
                                    p.getFirstName(),
                                    p.getNumerKartoteki().getId(),
                                    p.getNumerStarejKartoteki() );
                        }

//                        log.info("numer kartoteki: " + p.getNumerKartoteki() + " " + person.getRegistryNumber().getRegistryNum());
//                        log.info("numer starej kartoteki: " + p.getNumerStarejKartoteki() + " " + person.getRegistryNumber().getOldRegistryNum());
                    }

                });
    }

}
