package com.evolve.gui.person.list.search;

import com.evolve.domain.Account;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.Unit;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public record PersonSearchCriteria(String unitNumber, Boolean hasDocuments,
                                   PersonStatus personStatus,
                                   Person.Gender personGender,
                                   Set<Account.AccountType> hasAccountTypes,
                                   Set<String> hasAccountUnits) {

    public static PersonSearchCriteria empty() {
        return new PersonSearchCriteria(null, null, null, null, Set.of(), Set.of());
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(unitNumber) &&
                hasDocuments == null &&
                personStatus == null &&
                personGender == null &&
                hasAccountUnits.isEmpty() &&
                hasAccountTypes.isEmpty();
    }

    @Override
    public String toString() {
        String result = "";

        if (StringUtils.isNotEmpty(unitNumber)) {
            result += "Jednostka: " + unitNumber;
        }

        if (hasDocuments != null) {
            final String yesNo = hasDocuments ? "Tak" : "Nie";
            result += " Załączniki: " + yesNo;
        }

        if (personStatus != null) {
            result += " Status: " + personStatus.getName();
        }

        if (personGender != null) {
            result += " Płeć: " + personGender.getName();
        }

        final Set<String> accountTypes = new HashSet<>();

        if (!hasAccountTypes.isEmpty()) {
            hasAccountTypes.stream()
                    .map(Account.AccountType::getDescription).forEach(accountTypes::add);
        }

        if (!hasAccountUnits.isEmpty()) {
            hasAccountUnits.stream()
                    .map(Unit::fromCode)
                    .forEach(accountTypes::add);
        }

        if (!accountTypes.isEmpty()) {
            result += " Typy kont: " + String.join(", ", accountTypes);
        }

        return result;
    }
}
