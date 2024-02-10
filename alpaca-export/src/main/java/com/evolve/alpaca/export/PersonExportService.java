package com.evolve.alpaca.export;

import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.services.PersonsService;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@Service
public class PersonExportService {
    private final PersonsService personsService;
    private final CustomMappingStrategy<PersonExportView> mappingStrategy = new CustomMappingStrategy<>();

    PersonExportService(PersonsService personsService) {
        this.personsService = personsService;
        this.mappingStrategy.setType(PersonExportView.class);
    }

    public void exportPersons(PersonExportCriteria criteria, File file, List<String> orderedList)  {
        log.info("export to {}", file.getAbsolutePath());
        var persons = fetchPersonsForExport(criteria.exportType(), orderedList);

        switch (criteria.exportTargetFormat()) {
            case CSV -> exportToCsv(file, persons);
            case JSON -> exportToJson(file, persons);
            case ODS -> log.warn("Not yet implemented!");
        }
    }

    void exportToJson(File file, List<Person> personList) {
        final String personJson = LogUtil.prettyPrintJson(personList);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(personJson);
            writer.flush();
        } catch (IOException ex) {
            throw new AlpacaExportException(ex);
        }
    }

    void exportToCsv(File file, List<Person> personList) {
        final List<PersonExportView> recordsForExport = personList.stream()
                .map(PersonExportView::of)
                .collect(Collectors.toList());
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writeToFile(writer, recordsForExport);
            writer.flush();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException |
                 IOException ex) {
            throw new AlpacaExportException(ex);
        }
    }

    List<Person> fetchPersonsForExport(PersonExportType exportType, List<String> orderedList) {
        final List<Person> allPersons = personsService.fetch(PersonLookupCriteria.ALL)
                .stream()
                .toList();
        if (exportType == PersonExportType.ALL) {
            return allPersons;
        }

        final Function<String, Optional<Person>> idToPerson = personId -> allPersons.stream()
                .filter(person -> personId.equals(person.getPersonId()))
                .findFirst();
        return emptyIfNull(orderedList)
                .stream()
                .map(idToPerson)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    List<PersonExportView> fetchRecordsToExport(PersonExportType exportType, List<String> orderedList) {
        final List<PersonExportView> allPersons = personsService.fetch(PersonLookupCriteria.ALL)
                .stream()
                .map(PersonExportView::of)
                .toList();
        if (exportType == PersonExportType.ALL) {
            return allPersons;
        }

        final Function<String, Optional<PersonExportView>> idToPerson = personId -> allPersons.stream()
                .filter(record -> personId.equals(record.getId()))
                .findFirst();
        return emptyIfNull(orderedList)
                .stream()
                .map(idToPerson)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    void writeToFile(Writer writer, List<PersonExportView> records) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        final StatefulBeanToCsv<PersonExportView> beanWriter = new StatefulBeanToCsvBuilder<PersonExportView>(writer)
                .withMappingStrategy(mappingStrategy)
                .build();
        beanWriter.write(records);
    }

    private static class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

        @Override
        public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
            final int numColumns = getFieldMap().values().size();
            super.generateHeader(bean);

            String[] header = new String[numColumns];

            BeanField<T, Integer> beanField;
            for (int i = 0; i < numColumns; i++) {
                beanField = findField(i);
                String columnHeaderName = extractHeaderName(beanField);
                header[i] = columnHeaderName;
            }
            return header;
        }

        private String extractHeaderName(final BeanField<T, Integer> beanField) {
            if (beanField == null
                    || beanField.getField() == null
                    || beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class).length == 0) {
                return StringUtils.EMPTY;
            }

            final CsvBindByName bindByNameAnnotation =
                    beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class)[0];
            return bindByNameAnnotation.column();
        }
    }


}