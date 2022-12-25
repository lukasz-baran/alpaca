package com.evolve.importing.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public class PersonReader {
    private static final List<String> IMIONA = List.of("STANISŁAW",  "ALICJA",
            "MARIA", "STANISŁAWA",
            "TERESA", "JERZY",
            "RAQEEB", "MACIEJ",
            "MICHAŁ", "PIOTR",
            "PAWEŁ", "GRAŻYNA",
            "HALINA");
    public static Optional<Person> fromLine(String line) {
        final StringTokenizer tokenizer = new StringTokenizer(line);
        final int tokensNumber = tokenizer.countTokens();

        // na razie ignorujemy wpisy bez osób
        if (tokensNumber == 1) {
            return Optional.empty();
        }

        final String kartoteka = fixNumerKartoteki(tokenizer.nextToken());
        final String jednostka = tokenizer.nextToken();
        final String grupa = tokenizer.nextToken();
        final String index = tokenizer.nextToken();

        final List<String> tokensLeft = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            tokensLeft.add(tokenizer.nextToken());
        }
        // tokensLeft w idealnym przypadku zawiera 2 elementy (imię i nazwisko)
        // jeśli zawiera 3 i ostatni jest liczbą numer to


        final String oldKartoteka = getKartotekaOldNumber(tokensLeft);

        final Boolean rezygnacja = czyRezygnacja(tokensLeft);

        final String secondName = secondName(tokensLeft);

        final String lastName = getLastName(tokensLeft);
        final String firstName = getFirstName(tokensLeft, Optional.ofNullable(lastName));

        //scanner.hasNext()
        final Person osoba = Person.builder()
                .numerKartoteki(KartotekaId.of(kartoteka))
                .numerJednostki(jednostka)
                .numerGrupy(grupa)
                .index(index)
                .numerStarejKartoteki(oldKartoteka)
                .lastName(lastName)
                .firstName(firstName)
                .secondName(secondName)
                .statusDetails(rezygnacja ? PersonStatusDetails.resigned() : PersonStatusDetails.active())
                .line(line)
                .build();

        return Optional.of(osoba);
    }

    static boolean czyRezygnacja(List<String> tokensLeft) {
        if (tokensLeft.size() > 2) {
            final int lastElementIndex = tokensLeft.size() - 1;
            final String lastElement = tokensLeft.get(lastElementIndex);
            if ("Rez.".equals(lastElement)) {
                tokensLeft.remove(lastElementIndex);
                return true;
            }
        }
        return false;
    }

    static String secondName(List<String> tokensLeft) {
        if (tokensLeft.size() > 2) {
            final int lastElementIndex = tokensLeft.size() - 1;
            final String lastElement = tokensLeft.get(lastElementIndex);
            if (IMIONA.contains(lastElement)) {
                tokensLeft.remove(lastElementIndex);
                return lastElement;
            }
        }
        return null;
    }

    static String fixNumerKartoteki(String numer) {
        if (numer.endsWith(".")) {
            return numer.replaceAll("\\.", "");
        }
        return numer;
    }

    // NOTE: it modifies given list
    static String getKartotekaOldNumber(List<String> tokensLeft) {
        if (tokensLeft.size() > 2) {
            final int lastElementIndex = tokensLeft.size() - 1;
            final String lastElement = tokensLeft.get(lastElementIndex);
            try {
                Integer.parseInt(lastElement);
                tokensLeft.remove(lastElementIndex);
                return lastElement;
            } catch (NumberFormatException nfe) {
                // ignore
            }
        }
        return null;
    }


    static String getLastName(List<String> tokensLeft) {
        if (tokensLeft.size() == 2) {
            return tokensLeft.get(0);
        }

        if (tokensLeft.size() > 2) {
            String first = tokensLeft.get(0);
            String second = tokensLeft.get(1).trim();

            if (first.endsWith("-")) {
                return first + second;
            }

            if ("-".equals(second)) {
                String third = tokensLeft.get(2);
                return first + second + third;
            }

            if (second.startsWith("-")) {
                return first + second;
            }
        }

        return null;
    }

    static String getFirstName(List<String> tokensLeft, Optional<String> maybeLastname) {
        if (tokensLeft.size() == 2) {
            return tokensLeft.get(1);
        }
        if (tokensLeft.size() > 2 && maybeLastname.isPresent()) {
            return tokensLeft.get(2);
        }

        return null;

    }


}
