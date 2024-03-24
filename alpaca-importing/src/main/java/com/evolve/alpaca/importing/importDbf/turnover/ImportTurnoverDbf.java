package com.evolve.alpaca.importing.importDbf.turnover;

import com.evolve.alpaca.importing.importDbf.DbfData;
import com.evolve.alpaca.importing.importDbf.DbfImportBase;
import com.linuxense.javadbf.DBFReader;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ImportTurnoverDbf extends DbfImportBase<DbfTurnover> {

    @Override
    public List<DbfTurnover> performImport(URL fileUrl) {
        return new ArrayList<>(super.performImport(fileUrl).subList(0, 100));
    }

    @Override
    public DbfData<DbfTurnover> onImporting(DBFReader reader, DbfData<DbfTurnover> dbfData) {
        final List<String> fieldNames = dbfData.getFields();
        System.out.println("fieldNames: " + fieldNames);
        Object[] rowObjects;

        while ((rowObjects = reader.nextRecord()) != null) {
            final Map<String, Object> rawData = new HashMap<>();
            for (int i = 0; i < rowObjects.length; i++) {
                final String fieldName = fieldNames.get(i);
                rawData.put(fieldName, rowObjects[i]);
            }
            dbfData.addItem(DbfTurnover.of(rawData));
        }
        return dbfData;
    }
}
