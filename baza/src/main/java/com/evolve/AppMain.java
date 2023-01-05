package com.evolve;

import com.evolve.importing.importDbf.DbfPerson;
import com.evolve.importing.importDbf.ImportDbfFile;
import com.evolve.importing.importDoc.ImportAlphanumeric;
import com.evolve.importing.importDoc.ImportPeople;
import com.evolve.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.importing.importDoc.person.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;

@Slf4j
public class AppMain {
    public static final URL FILE_BY_ALPHA = Resources.getResource("ludzie-alfabetycznie.txt");
    public static final URL DBF_FILE = Resources.getResource("Z_B_KO.DBF");

    @SneakyThrows
    public static void main(String[] args) {
        new AppMain().imports();
        //new NitrateStarter().starter();
    }

    @SneakyThrows
    public void imports() {
        final List<Person> people = new ImportPeople().processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric(new File(FILE_BY_ALPHA.getFile()))
                .processFile();

        final List<DbfPerson> osobyDbf = new ImportDbfFile()
                .performImport(DBF_FILE.getPath())
                .getOsoby();

        log.info("Wczytano " + people.size() + " z indeksu");
        log.info("Wczytano " + grupyAlfabetyczne.getSize() + " z grup alfabetycznych");
        log.info("Wczytano " + osobyDbf.size());


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("osobyDbf.json"), osobyDbf);

    }

}
