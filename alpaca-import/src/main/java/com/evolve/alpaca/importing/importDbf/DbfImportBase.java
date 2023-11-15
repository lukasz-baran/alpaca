package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.exception.ImportFailedException;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DbfImportBase<T> {

    public static final Charset DBF_CHARSET = Charset.forName("Cp1250");

    public final DbfData<T> performImport(String filePath) {
        try (final FileInputStream fileInputStream = new FileInputStream(filePath);
            final DBFReader reader = new DBFReader(fileInputStream, DBF_CHARSET)) {

            final int numberOfFields = reader.getFieldCount();
            final List<DBFField> fields = readColumns(reader);
            final List<String> fieldNames = getNames(fields);
            final DbfData<T> dbfData = new DbfData<>(fieldNames);

            return onImporting(reader, dbfData);
        } catch (DBFException | IOException e) {
            throw new ImportFailedException("failed to import data from dbf file", e);
        }
    }

    public abstract DbfData<T> onImporting(DBFReader reader, DbfData<T> dbfData);

    protected List<String> getFields(String filePath) throws Exception {
        DBFReader reader = new DBFReader(new FileInputStream(filePath), Charset.forName("Cp1250"));

        final int numberOfFields = reader.getFieldCount();
        final List<DBFField> fields = readColumns(reader);
        final List<String> fieldNames = getNames(fields);

        return fieldNames;
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
