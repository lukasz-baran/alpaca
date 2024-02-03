package com.evolve.alpaca.export;

import com.evolve.domain.PersonLookupCriteria;
import com.evolve.services.PersonsService;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Service
public class PersonExportService {
    private final PersonsService personsService;
    private final CustomMappingStrategy<PersonExportView> mappingStrategy = new CustomMappingStrategy<>();

    PersonExportService(PersonsService personsService) {
        this.personsService = personsService;
        this.mappingStrategy.setType(PersonExportView.class);
    }

    public void exportPersons(File file)  {
        log.info("export to {}", file.getAbsolutePath());

        List<PersonExportView> allPersons = personsService.fetch(PersonLookupCriteria.ALL)
                .stream()
                .map(PersonExportView::of)
                .toList();

        try (var writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writeToFile(writer, allPersons);
            writer.flush();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException |
                 IOException ex) {
            throw new AlpacaExportException(ex);
        }
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