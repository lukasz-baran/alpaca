package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.linuxense.javadbf.DBFReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ImportPersonDbf extends DbfImportBase<DbfPerson> {

    @Override
    public DbfData<DbfPerson> onImporting(DBFReader reader, DbfData<DbfPerson> dbfData) {
        final List<String> fieldNames = dbfData.getFields();

        Object[] rowObjects;

        while ((rowObjects = reader.nextRecord()) != null) {
            //log.debug("number of objects {}", rowObjects.length);

            Map<String, Object> personData = new HashMap<>();

            for (int i = 0; i < rowObjects.length; i++) {
                final String fieldName = fieldNames.get(i);
//                final DBFField fieldInfo = fields.get(i);
                personData.put(fieldName, rowObjects[i]);
            }

            //log.info("person {}", personData);
            dbfData.addItem(DbfPerson.of(personData));
        }

        //log.info("Field names: {}", fieldNames);
        return dbfData;
    }

}
