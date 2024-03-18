package com.evolve.alpaca.importing.importDbf;

import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import org.junit.jupiter.api.Test;

import static com.evolve.domain.PersonAssertion.assertPerson;

class RegistryNumberFixerShould {

    @Test
    void useOnlyOneRegistryNumber() {
        // given
        Person person = Person.builder()
                .personId("17087")
                .registryNumber(RegistryNumber.of(83))
                .build();

        // when
        RegistryNumberFixer.fixPersonRegistryNumbers(person, RegistryNumber.of(83));

        // then
        assertPerson(person)
                .hasRegistryNumber(RegistryNumber.of(83))
                .hasNoOldRegistryNumber();
    }

    @Test
    void detectProperRegistryNumbers() {
        // given -- number is switched
        final Person halina = Person.builder()
                .personId("halina")
                .registryNumber(RegistryNumber.of(1246))
                .oldRegistryNumber(RegistryNumber.of(100))
                .build();

        // when
        RegistryNumberFixer.fixPersonRegistryNumbers(halina, RegistryNumber.of(100));

        // then
        assertPerson(halina)
                .hasRegistryNumber(RegistryNumber.of(100))
                .hasOldRegistryNumber(RegistryNumber.of(1246));

        // given
        final Person jadwiga = Person.builder()
                .personId("jadwiga")
                .registryNumber(RegistryNumber.of(100))
                .oldRegistryNumber(RegistryNumber.of(1069))
                .build();

        // when
        RegistryNumberFixer.fixPersonRegistryNumbers(jadwiga, RegistryNumber.of(1069));

        // then
        assertPerson(jadwiga)
                .hasRegistryNumber(RegistryNumber.of(1069))
                .hasOldRegistryNumber(RegistryNumber.of(100));
    }

}