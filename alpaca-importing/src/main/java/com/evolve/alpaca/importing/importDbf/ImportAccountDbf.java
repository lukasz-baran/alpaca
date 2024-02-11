package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.linuxense.javadbf.DBFReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportAccountDbf extends DbfImportBase<DbfAccount> {

    @Override
    public DbfData<DbfAccount> onImporting(DBFReader reader, DbfData<DbfAccount> dbfData) {
        final List<String> fieldNames = dbfData.getFields();
        System.out.println("fieldNames: " + fieldNames);


        Object[] rowObjects;

        while ((rowObjects = reader.nextRecord()) != null) {
            //log.debug("number of objects {}", rowObjects.length);

            Map<String, Object> personData = new HashMap<>();

            for (int i = 0; i < rowObjects.length; i++) {
                final String fieldName = fieldNames.get(i);
                personData.put(fieldName, rowObjects[i]);
            }

            //log.info("person {}", personData);
            dbfData.addItem(DbfAccount.of(personData));
        }

        //log.info("Field names: {}", fieldNames);
        return dbfData;
    }
}
