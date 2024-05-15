package com.evolve.gui.person.preview;

import com.evolve.domain.Person;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class PersonTreeItem {
    public static final String MISSING = "<brak>";

    private final String tag;
    private final Function<Person, Optional<String>> toText;
    private final Person person;

    public static PersonTreeItem withoutTag(Function<Person, Optional<String>> toText, Person person) {
        return new PersonTreeItem(null, toText, person);
    }

    @Override
    public String toString() {
        return tag + ": " + toText.apply(person).orElse(MISSING);
    }
}
