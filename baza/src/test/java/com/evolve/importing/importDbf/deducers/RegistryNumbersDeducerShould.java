package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.RegistryNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RegistryNumbersDeducerShould {

    @Mock
    IssuesLogger.ImportIssues importIssues;

    @InjectMocks
    RegistryNumbersDeducer numbersDeducer;

    @Test
    void deduceSimpleCase() {
        var result = numbersDeducer.deduceFrom(List.of(" 1 520  1876"));

        assertThat(result)
                .hasValue(new RegistryNumber(1520, 1876));
    }

    @Test
    void deduceTwoNumbers() {
        var result = numbersDeducer.deduceFrom(List.of("   154  2564"));

        assertThat(result)
                .hasValue(new RegistryNumber(154, 2564));
    }

    @Test
    void deduceOneNumber() {
        var result = numbersDeducer.deduceFrom(List.of("   581"));

        assertThat(result)
                .hasValue(new RegistryNumber(581, null));
    }

    @Test
    void noNumbersOnlyDots() {
        var result = numbersDeducer.deduceFrom(List.of("  .........."));

        assertThat(result)
                .isEmpty();
    }

    @Test
    void onlyNewRegistryNumber() {
        var result = numbersDeducer.deduceFrom(List.of("   ...  1896"));

        assertThat(result)
                .hasValue(new RegistryNumber(null, 1896));

        result = numbersDeducer.deduceFrom(List.of("        0211"));

        assertThat(result)
                .hasValue(new RegistryNumber(null, 211));
    }

    @Test
    void rejectOtherInput() {
        assertThat(numbersDeducer.deduceFrom(List.of("813-03-34-838")))
                .isEmpty();
    }

}