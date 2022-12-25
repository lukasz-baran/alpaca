package com.evolve.importDbf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class DbfData {
    private final List<String> fields;
    private final List<DbfPerson> osoby = new ArrayList<>();

    public void addPerson(DbfPerson person) {
        osoby.add(person);
    }

}
