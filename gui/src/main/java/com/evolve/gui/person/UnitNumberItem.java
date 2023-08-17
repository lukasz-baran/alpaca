package com.evolve.gui.person;

public record UnitNumberItem(String unitNumber, String unitName) {
    public static final UnitNumberItem ALL = new UnitNumberItem("ALL", null);

    @Override
    public String toString() {
        if (this.equals(ALL)) {
            return "(wszystkie jednostki)";
        }
        return unitNumber + " - " + unitName;
    }
}
