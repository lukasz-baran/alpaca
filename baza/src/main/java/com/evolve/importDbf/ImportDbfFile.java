package com.evolve.importDbf;

import com.evolve.importing.exception.ImportFailedException;
import com.linuxense.javadbf.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImportDbfFile {

    public DbfData performImport(String filePath) {
        DBFReader reader = null;
        try {
            //reader = new DBFReader(new FileInputStream(filePath), Charset.forName("ISO-8859-2"));
            reader = new DBFReader(new FileInputStream(filePath), Charset.forName("Cp1250"));


            final int numberOfFields = reader.getFieldCount();
            final List<DBFField> fields = readColumns(reader);
            final List<String> fieldNames = getNames(fields);
            final DbfData dbfData = new DbfData(fieldNames);

            Object[] rowObjects;

            while ((rowObjects = reader.nextRecord()) != null) {
                System.out.println(rowObjects.length);

                Map<String, Object> personData = new HashMap<>();

                for (int i = 0; i < rowObjects.length; i++) {
                    final String fieldName = fieldNames.get(i);
                    final DBFField fieldInfo = fields.get(i);

                    personData.put(fieldName, rowObjects[i]);
                }

                System.out.println(personData);
                dbfData.addPerson(DbfPerson.of(personData));
            }

            System.out.println(fieldNames);
            //System.out.println(dbfData);

            return dbfData;

        } catch (DBFException | IOException e) {
            throw new ImportFailedException("failed to import data from dbf file", e);
        } finally {
            DBFUtils.close(reader);
        }
    }

    private List<DBFField> readColumns(DBFReader reader) {
        final int numberOfFields = reader.getFieldCount();
        final List<DBFField> columns = new ArrayList<>();
        for (int i = 0; i < numberOfFields; i++) {
            DBFField field = reader.getField(i);
            columns.add(field);
        }
        return columns;

    }

    private List<String> getNames(List<DBFField> fields ) {
        return fields.stream().map(DBFField::getName).collect(Collectors.toList());
    }

}
