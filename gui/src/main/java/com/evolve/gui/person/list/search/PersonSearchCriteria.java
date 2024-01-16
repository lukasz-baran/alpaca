package com.evolve.gui.person.list.search;

import com.evolve.domain.PersonStatus;
import org.apache.commons.lang3.StringUtils;

public record PersonSearchCriteria(String unitNumber, Boolean hasDocuments, PersonStatus personStatus) {

    public static PersonSearchCriteria empty() {
        return new PersonSearchCriteria(null, null, null);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(unitNumber) && hasDocuments == null && personStatus == null;
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

        return result;
    }
}
