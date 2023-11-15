package com.evolve.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Group {
    private final String numer;
    private final Set<Character> litery;

    public static List<Group> GRUPY = List.of(
            Group.of("01", Set.of('A')),
            Group.of("02", Set.of('B')),
            Group.of("03", Set.of('C', 'Ć')),
            Group.of("04", Set.of('D')),
            Group.of("05", Set.of('E')),
            Group.of("06", Set.of('F')),
            Group.of("07", Set.of('G')),
            Group.of("08", Set.of('H')),
            Group.of("09", Set.of('I')),
            Group.of("10", Set.of('J')),
            Group.of("11", Set.of('K')),
            Group.of("12", Set.of('L', 'Ł')),
            Group.of("13", Set.of('M')),
            Group.of("14", Set.of('N')),
            Group.of("15", Set.of('O' ,'Ó')),
            Group.of("16", Set.of('P')),
            Group.of("17", Set.of('R')),
            Group.of("18", Set.of('S')),
            Group.of("19", Set.of('Ś')),
            Group.of("20", Set.of('T')),
            Group.of("21", Set.of('U')),
            Group.of("22", Set.of('W')),
            Group.of("23", Set.of('Z')),
            Group.of("24", Set.of('Ż', 'Ź')));

    public static Group of(String numer, Set<Character> litery) {
        return new Group(numer, litery);
    }

    public static Optional<Group> groupFor(Character character) {
        return GRUPY.stream().filter(grupa -> grupa.litery.contains(character))
                .findFirst();
    }

    public static Set<Character> allowedCharacters() {
        return GRUPY.stream()
                .map(group -> group.litery)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public static boolean isAllowedCharacter(Character character) {
        return allowedCharacters().contains(character);
    }

}
