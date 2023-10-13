package com.evolve.alpaca.importing.importDbf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class DbfData<T> {
    private final List<String> fields;
    private final List<T> items = new ArrayList<>();

    public void addItem(T item) {
        items.add(item);
    }

}
