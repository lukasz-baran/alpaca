package com.evolve.alpaca.importing.importDbf.account;

import com.evolve.alpaca.importing.importDbf.DbfData;
import com.evolve.alpaca.importing.importDbf.DbfImportBase;
import com.linuxense.javadbf.DBFReader;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ImportAccountDbf extends DbfImportBase<DbfAccount> {

    @Override
    public DbfData<DbfAccount> onImporting(DBFReader reader, DbfData<DbfAccount> dbfData) {
        final List<String> fieldNames = dbfData.getFields();
        System.out.println("fieldNames: " + fieldNames);
        Object[] rowObjects;

        while ((rowObjects = reader.nextRecord()) != null) {
            final Map<String, Object> personData = new HashMap<>();

            for (int i = 0; i < rowObjects.length; i++) {
                final String fieldName = fieldNames.get(i);
                personData.put(fieldName, rowObjects[i]);
            }
            dbfData.addItem(DbfAccount.of(personData));
        }
        return dbfData;
    }
}
