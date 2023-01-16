package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.Person;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class AuthorizedPersonDeducer extends AbstractSmartDeducer<List<Person.AuthorizedPerson>> {

    private static final Set<PersonRelative> RELATIVE_TYPES = Set.of(
            PersonRelative.of("ż.", "żona"),
            PersonRelative.of("m.", "mąż"),
            PersonRelative.of("s.", "syn"),
            PersonRelative.of("c.", "córka"),
            PersonRelative.of("c,", "córka"),
            PersonRelative.of("mt.", "matka")
        );
    // TODO handle other relations
    // TODO handle two relatives ->
    //  "c.Małgorzata,c.Katarzyna"
    //  "c.Anna s.Andrzej"
    //  "c.Anna s,Zygmunt s.Andrze"


    private static final Predicate<String> isAnyRelation = guess -> RELATIVE_TYPES.stream().anyMatch(relativeType -> relativeType.isRelation().test(guess));

    public AuthorizedPersonDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<List<Person.AuthorizedPerson>> deduceFrom(List<String> guesses) {
        return RELATIVE_TYPES.stream()
                .map(relativeType -> relativeType.deduceAuthorizedPerson(guesses))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(List::of);
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses.stream()
                .filter(not(isAnyRelation))
                .collect(Collectors.toList());
    }

    public record PersonRelative(String distinct, String relation) {
        static PersonRelative of(String distinct, String relation) {
            return new PersonRelative(distinct, relation);
        }

        Predicate<String> isRelation() {
            return s -> s.startsWith(this.distinct);
        }

        Optional<Person.AuthorizedPerson> deduceAuthorizedPerson(List<String> guesses) {
            final Optional<String> maybeRelation = guesses.stream()
                    .filter(isRelation())
                    .findFirst();
            return maybeRelation.map(goodGuess -> {
                        final String trimmed = StringUtils.removeStart(goodGuess, this.distinct);
                        return trimmed.split(" ");
                    })
                    .filter(afterSplit -> afterSplit.length > 1)
                    .map(afterSplit -> new Person.AuthorizedPerson(afterSplit[0], afterSplit[1], this.relation, null, null, null));

        }

    }

}
