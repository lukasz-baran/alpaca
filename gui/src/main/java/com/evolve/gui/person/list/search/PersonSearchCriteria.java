package com.evolve.gui.person.list.search;

import org.apache.commons.lang3.StringUtils;

public record PersonSearchCriteria(String unitNumber) {

    public static PersonSearchCriteria empty() {
        return new PersonSearchCriteria(null);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(unitNumber);
    }

    @Override
    public String toString() {
        return "Jednostka: " + unitNumber;
    }
}
