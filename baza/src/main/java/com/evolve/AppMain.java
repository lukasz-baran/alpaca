package com.evolve;

import com.evolve.importDbf.DbfPerson;
import com.evolve.importDbf.ImportDbfFile;
import com.evolve.importing.ImportAlphanumeric;
import com.evolve.importing.ImportPeople;
import com.evolve.importing.group.GrupyAlfabetyczne;
import com.evolve.importing.person.Person;
import com.evolve.repo.NitrateStarter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.repository.ObjectRepository;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.dizitart.no2.collection.Document.createDocument;

public class AppMain {
    public static final URL FILE_BY_ALPHA = Resources.getResource("ludzie-alfabetycznie.txt");
    public static final URL DBF_FILE = Resources.getResource("Z_B_KO.DBF");

    @SneakyThrows
    public static void main(String[] args) {
        //new AppMain().imports();
        new NitrateStarter().starter();
    }

    @SneakyThrows
    public void imports() {
        final List<Person> people = new ImportPeople().processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric(new File(FILE_BY_ALPHA.getFile()))
                .processFile();

        final List<DbfPerson> osobyDbf = new ImportDbfFile()
                .performImport(DBF_FILE.getPath())
                .getOsoby();

        System.out.println("Wczytano " + people.size() + " z indeksu");
        System.out.println("Wczytano " + grupyAlfabetyczne.getSize() + " z grup alfabetycznych");
        System.out.println("Wczytano " + osobyDbf.size());


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("osobyDbf.json"), osobyDbf);

    }

}
