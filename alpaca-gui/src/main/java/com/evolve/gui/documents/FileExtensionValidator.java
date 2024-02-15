package com.evolve.gui.documents;

import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Check whether file name has extension (if not respond with error) and whether the file extension has proper type.
 */
@RequiredArgsConstructor
public class FileExtensionValidator implements Validator<String> {
    private final String originalFileName;

    @Override
    public ValidationResult validate(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return ValidationResult.of("Nazwa pliku nie może być pusta");
        }

        final String extension = Files.getFileExtension(fileName);

        if (StringUtils.isBlank(extension)) {
            return ValidationResult.of("Niepoprawne rozszerzenie");
        }

        final String originalExtension = Files.getFileExtension(originalFileName);
        if (!StringUtils.equalsIgnoreCase(extension, originalExtension)) {
            return ValidationResult.of("Rozszerzenie nie może być zmienione");
        }

        return ValidationResult.empty();
    }

}
