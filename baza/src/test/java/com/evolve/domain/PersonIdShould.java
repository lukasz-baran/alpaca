package com.evolve.domain;

import org.dizitart.no2.common.util.ObjectUtils;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class PersonIdShould {

    private static final VarHandle MODIFIERS;

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    void beUsedAsNitriteId() throws NoSuchFieldException {
        var emptyElementDataField = PersonId.class.getDeclaredField("groupNumber");
        // make field non-final
        MODIFIERS.set(emptyElementDataField, emptyElementDataField.getModifiers() & ~Modifier.FINAL);

         emptyElementDataField = PersonId.class.getDeclaredField("index");
        // make field non-final
        MODIFIERS.set(emptyElementDataField, emptyElementDataField.getModifiers() & ~Modifier.FINAL);

        ObjectUtils.newInstance(PersonId.class, true);
    }

}