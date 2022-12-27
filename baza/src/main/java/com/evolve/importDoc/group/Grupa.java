package com.evolve.importDoc.group;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Grupa {
    private final String numer;
    private final Set<Character> litery;

    public static List<Grupa> GRUPY = List.of(
            Grupa.of("01", Set.of('A')),
            Grupa.of("02", Set.of('B')),
            Grupa.of("03", Set.of('C', 'Ć')),
            Grupa.of("04", Set.of('D')),
            Grupa.of("05", Set.of('E')),
            Grupa.of("06", Set.of('F')),
            Grupa.of("07", Set.of('G')),
            Grupa.of("08", Set.of('H')),
            Grupa.of("09", Set.of('I')),
            Grupa.of("10", Set.of('J')),
            Grupa.of("11", Set.of('K')),
            Grupa.of("12", Set.of('L', 'Ł')),
            Grupa.of("13", Set.of('M')),
            Grupa.of("14", Set.of('N')),
            Grupa.of("15", Set.of('O' ,'Ó')),
            Grupa.of("16", Set.of('P')),
            Grupa.of("17", Set.of('R')),
            Grupa.of("18", Set.of('S')),
            Grupa.of("19", Set.of('Ś')),
            Grupa.of("20", Set.of('T')),
            Grupa.of("21", Set.of('U')),
            Grupa.of("22", Set.of('W')),
            Grupa.of("23", Set.of('Z')),
            Grupa.of("24", Set.of('Ż', 'Ź')));

    public static Grupa of(String numer, Set<Character> litery) {
        return new Grupa(numer, litery);
    }

    public static Optional<Grupa> groupFor(Character character) {
        return GRUPY.stream().filter(grupa -> grupa.litery.contains(character))
                .findFirst();
    }

}
