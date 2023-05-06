package com.evolve;

import com.evolve.importing.importDbf.DbfPerson;
import com.evolve.importing.importDbf.ImportDbfFile;
import com.evolve.importing.importDoc.ImportAlphanumeric;
import com.evolve.importing.importDoc.ImportPeople;
import com.evolve.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.importing.importDoc.person.Person;
import com.evolve.services.PersonsFactory;
import com.evolve.utils.LogUtil;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;

import static com.evolve.importing.importDoc.ImportAlphanumeric.FILENAME_BY_ALPHA;
import static com.evolve.importing.importDoc.ImportPeople.FILENAME_BY_NUMBERS;

@Slf4j
public class AppMain {
    public static final URL DBF_FILE = Resources.getResource("Z_B_KO.DBF");

    @SneakyThrows
    public static void main(String[] args) {
        new AppMain().imports();
        //new NitrateStarter().starter();
    }

    @SneakyThrows
    public void imports() {
        final List<Person> people = new ImportPeople(true).processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric()
                .processFile();

        final List<DbfPerson> osobyDbf = new ImportDbfFile()
                .performImport(DBF_FILE.getPath())
                .getOsoby();

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
