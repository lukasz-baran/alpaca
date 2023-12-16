package com.evolve.gui.person.list.search;

import org.apache.commons.lang3.StringUtils;

public record PersonSearchCriteria(String unitNumber, Boolean hasDocuments) {

    public static PersonSearchCriteria empty() {
        return new PersonSearchCriteria(null, null);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(unitNumber) && hasDocuments == null;
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

        return result;
    }
}
