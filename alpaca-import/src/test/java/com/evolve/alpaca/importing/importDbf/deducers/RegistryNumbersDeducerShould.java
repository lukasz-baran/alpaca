package com.evolve.alpaca.importing.importDbf.deducers;

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
                .hasValue(new RegistryNumber(1876, 1520));
    }


    @Test
    void deduceFromFourNumbers() {
        var result = numbersDeducer.deduceFrom(List.of(" 1 211 18 0"));

        assertThat(result)
                .hasValue(new RegistryNumber(180, 1211));
    }

    @Test
    void deduceTwoNumbers() {
        var result = numbersDeducer.deduceFrom(List.of("   154  2564"));

        assertThat(result)
                .hasValue(new RegistryNumber( 2564, 154));
    }

    @Test
    void deduceOneNumber() {
        var result = numbersDeducer.deduceFrom(List.of("   581"));

        assertThat(result)
                .hasValue(new RegistryNumber(null, 581));
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
                .hasValue(new RegistryNumber(1896, null));

        result = numbersDeducer.deduceFrom(List.of("        0211"));

        assertThat(result)
                .hasValue(new RegistryNumber(211, null));
    }

    @Test
    void rejectOtherInput() {
        assertThat(numbersDeducer.deduceFrom(List.of("813-03-34-838")))
                .isEmpty();
    }

}