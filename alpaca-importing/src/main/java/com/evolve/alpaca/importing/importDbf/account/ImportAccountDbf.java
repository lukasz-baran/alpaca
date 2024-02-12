package com.evolve.alpaca.importing.importDbf.account;

import com.evolve.alpaca.importing.importDbf.DbfData;
import com.evolve.alpaca.importing.importDbf.DbfImportBase;
import com.evolve.alpaca.importing.importDbf.account.DbfAccount;
import com.linuxense.javadbf.DBFReader;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ImportAccountDbf extends DbfImportBase<DbfAccount> {

    public static void importAccounts(URL url) {
        final List<DbfAccount> kontaDbf = new ImportAccountDbf()
                .performImport(url.getPath())
                .getItems();
        for (DbfAccount dbfAccount : kontaDbf) {
            System.out.println(dbfAccount);
        }
    }

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
