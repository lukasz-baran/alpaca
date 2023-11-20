package com.evolve.alpaca.importing.importDbf;

import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class RegistryNumberFixer {

    public static void fixPersonRegistryNumbers(final Person person, final RegistryNumber kartotekaId) {
        final String registryNumber = Optional.ofNullable(person.getRegistryNumber())
                .map(RegistryNumber::getRegistryNum)
                .map(Objects::toString)
                .orElse("");

        final String oldRegistryNumber = Optional.ofNullable(person.getOldRegistryNumber())
                .map(RegistryNumber::getRegistryNum)
                .map(Objects::toString)
                .orElse("");

        if (kartotekaId.getNumber().isPresent()) {
            if (StringUtils.equals(kartotekaId.getRegistryNum().toString(), registryNumber)) {
                log.info("personId " + person.getPersonId() + " has correct registry number");
            } else {
                log.info("personId " + person.getPersonId() +
                        " has INVALID registry number: " + kartotekaId.getRegistryNum().toString() + " " +
                        registryNumber + " old: " + oldRegistryNumber);

                if (StringUtils.equals(kartotekaId.getRegistryNum().toString(), oldRegistryNumber)) {
                    person.setRegistryNumber(RegistryNumber.fromText(oldRegistryNumber));
                    person.setOldRegistryNumber(RegistryNumber.fromText(registryNumber));
                }

            }
        } else {
            if (StringUtils.isNotBlank(registryNumber) && StringUtils.isBlank(oldRegistryNumber)) {
                person.setOldRegistryNumber(person.getRegistryNumber());
                person.setRegistryNumber(null);
            }
        }

    }
}
