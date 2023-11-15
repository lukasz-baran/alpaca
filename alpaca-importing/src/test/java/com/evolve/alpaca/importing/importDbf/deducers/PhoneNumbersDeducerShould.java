package com.evolve.alpaca.importing.importDbf.deducers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumbersDeducerShould {

    @Test
    void verifyIfDotsEntries() {
        assertThat(PhoneNumbersDeducer.containsOnlyDots(".")).isTrue();
        assertThat(PhoneNumbersDeducer.containsOnlyDots("...")).isTrue();
        assertThat(PhoneNumbersDeducer.containsOnlyDots("12.")).isFalse();
    }

}