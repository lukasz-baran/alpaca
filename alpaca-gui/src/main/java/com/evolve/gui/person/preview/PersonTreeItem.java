package com.evolve.gui.person.preview;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class PersonTreeItem {
    private final String tag;

    private final Optional<Object> value;
    private final String displayText;

    @Setter
    private PersonTreeItemDifference difference = PersonTreeItemDifference.SAME;

    public static PersonTreeItem withTag(String tag, Optional<Object> value, String displayText) {

        return new PersonTreeItem(tag, value, displayText);

    }

    @Override
    public String toString() {
        if (tag == null) {
            return displayText;
        }

        return tag + ": " + displayText;
    }
}
