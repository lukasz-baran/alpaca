package com.evolve;

import com.evolve.alpaca.importing.importDbf.ImportAccountDbf;
import com.evolve.alpaca.importing.importDbf.ImportPersonDbf;
import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.alpaca.importing.importDoc.ImportAlphanumeric;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;

@Slf4j
public class AppMain {
    public static final URL PERSONS_DBF_FILE = Resources.getResource("Z_B_KO.DBF");
    public static final URL ACCOUNTS_DBF_FILE = Resources.getResource("PLAN.DBF");

    @SneakyThrows
    public static void main(String[] args) {
        new AppMain().imports();
        //new AppMain().importAccounts();
    }

    public void importAccounts() {
        final List<DbfAccount> kontaDbf = new ImportAccountDbf()
                .performImport(ACCOUNTS_DBF_FILE.getPath())
                .getItems();
        for (DbfAccount dbfAccount : kontaDbf) {
            System.out.println(dbfAccount);
        }
    }

    @SneakyThrows
    public void imports() {
        final List<PersonFromDoc> people = new ImportPeople(true).processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric()
                .processFile();

        final List<DbfPerson> osobyDbf = new ImportPersonDbf()
                .performImport(PERSONS_DBF_FILE.getPath())
                .getItems();

        var numery = osobyDbf.stream().map(DbfPerson::getNR_IDENT).toList();
        numery.forEach(numer -> {
            System.out.println("\"" + numer + "\"");
        });
        //System.out.println(numery);

//        final List<com.evolve.domain.Person> persons = new PersonsFactory().from(osobyDbf);
//
//        log.info("Wczytano " + people.size() + " z indeksu " + FILENAME_BY_NUMBERS);
//        log.info("Wczytano " + grupyAlfabetyczne.getSize() + " z grup alfabetycznych " + FILENAME_BY_ALPHA);
//        log.info("Wczytano " + osobyDbf.size());
//
//        persons.forEach(person -> {
//           verifyData(person, people, grupyAlfabetyczne);
//        });
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValue(new File("osobyDbf.json"), osobyDbf);
//
//        List<com.evolve.domain.Person> osoby =new PersonsFactory().from(osobyDbf);

        //System.out.println(osoby);

    }

}
