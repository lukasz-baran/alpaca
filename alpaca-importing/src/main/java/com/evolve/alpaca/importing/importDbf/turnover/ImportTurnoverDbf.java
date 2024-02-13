package com.evolve.alpaca.importing.importDbf.turnover;

import com.evolve.alpaca.importing.importDbf.DbfData;
import com.evolve.alpaca.importing.importDbf.DbfImportBase;
import com.evolve.alpaca.turnover.Turnover;
import com.linuxense.javadbf.DBFReader;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ImportTurnoverDbf extends DbfImportBase<DbfTurnover> {

    public static List<Turnover> performImport(URL turnoversFile) {
        final List<DbfTurnover> kontaDbf = new ImportTurnoverDbf()
                .performImport(turnoversFile.getPath())
                .getItems();

        return kontaDbf.subList(0, 100).stream()
                .map(DbfTurnover::of)
                .collect(Collectors.toList());
    }

    @Override
    public DbfData<DbfTurnover> onImporting(DBFReader reader, DbfData<DbfTurnover> dbfData) {
        final List<String> fieldNames = dbfData.getFields();
        System.out.println("fieldNames: " + fieldNames);

        Object[] rowObjects;

        while ((rowObjects = reader.nextRecord()) != null) {
            //log.debug("number of objects {}", rowObjects.length);

            Map<String, Object> rawData = new HashMap<>();

            for (int i = 0; i < rowObjects.length; i++) {
                final String fieldName = fieldNames.get(i);
                rawData.put(fieldName, rowObjects[i]);
            }

            //log.info("person {}", personData);
            dbfData.addItem(DbfTurnover.of(rawData));
        }

        //log.info("Field names: {}", fieldNames);
        return dbfData;
    }
}
