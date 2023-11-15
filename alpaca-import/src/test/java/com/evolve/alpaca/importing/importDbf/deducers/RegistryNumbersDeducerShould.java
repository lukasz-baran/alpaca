package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.RegistryNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RegistryNumbersDeducerShould {

    @Mock
    IssuesLogger.ImportIssues importIssues;

    RegistryNumbersDeducer newRegistryNumbersDeducer;
    RegistryNumbersDeducer oldwRegistryNumbersDeducer;

    @BeforeEach
    public void setUp() {
        newRegistryNumbersDeducer = new RegistryNumbersDeducer(importIssues, RegistryNumbersDeducer.RegistryNumberType.NEW);
        oldwRegistryNumbersDeducer = new RegistryNumbersDeducer(importIssues, RegistryNumbersDeducer.RegistryNumberType.OLD);
    }

    @Test
    void deduceSimpleCase() {
        var newNumber = newRegistryNumbersDeducer.deduceFrom(List.of(" 1 520  1876"));

        assertThat(newNumber)
                .hasValue(RegistryNumber.of(1876));

        var oldNumber = oldwRegistryNumbersDeducer.deduceFrom(List.of(" 1 520  1876"));

        assertThat(oldNumber)
                .hasValue(RegistryNumber.of(1520));
    }


//    @Test
//    void deduceFromFourNumbers() {
//        var result = newRegistryNumbersDeducer.deduceFrom(List.of(" 1 211 18 0"));
//
//        assertThat(result)
//                .hasValue(new RegistryNumber(180, 1211));
//    }
//
//    @Test
//    void deduceTwoNumbers() {
//        var result = newRegistryNumbersDeducer.deduceFrom(List.of("   154  2564"));
//
//        assertThat(result)
//                .hasValue(new RegistryNumber( 2564, 154));
//    }
//
//    @Test
//    void deduceOneNumber() {
//        var result = newRegistryNumbersDeducer.deduceFrom(List.of("   581"));
//
//        assertThat(result)
//                .hasValue(new RegistryNumber(null, 581));
//    }
//
//    @Test
//    void noNumbersOnlyDots() {
//        var result = newRegistryNumbersDeducer.deduceFrom(List.of("  .........."));
//
//        assertThat(result)
//                .isEmpty();
//    }
//
//    @Test
//    void onlyNewRegistryNumber() {
//        var result = newRegistryNumbersDeducer.deduceFrom(List.of("   ...  1896"));
//
//        assertThat(result)
//                .hasValue(new RegistryNumber(1896, null));
//
//        result = newRegistryNumbersDeducer.deduceFrom(List.of("        0211"));
//
//        assertThat(result)
//                .hasValue(new RegistryNumber(211, null));
//    }

    @Test
    void rejectOtherInput() {
        assertThat(newRegistryNumbersDeducer.deduceFrom(List.of("813-03-34-838")))
                .isEmpty();
    }

}