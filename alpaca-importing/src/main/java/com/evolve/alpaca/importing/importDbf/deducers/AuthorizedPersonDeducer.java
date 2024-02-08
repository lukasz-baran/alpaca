package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.Person;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AuthorizedPersonDeducer extends AbstractSmartDeducer<List<Person.AuthorizedPerson>> {

    private static final Set<PersonRelative> RELATIVE_TYPES = Set.of(
            PersonRelative.of("ż.", "żona"),
            PersonRelative.of("up. ż.", "żona"),
            PersonRelative.of("up.ż.", "żona"),
            PersonRelative.of("up. żona", "żona"),
            PersonRelative.of("m.", "mąż"),
            PersonRelative.of("up. mąż", "mąż"),
            PersonRelative.of("up. m.", "mąż"),
            PersonRelative.of("s.", "syn"),
            PersonRelative.of("up. syn", "syn"),
            PersonRelative.of("c.", "córka"),
            PersonRelative.of("c,", "córka"),
            PersonRelative.of("brat", "brat"),
            PersonRelative.of("up. brat", "brat"),
            PersonRelative.of("stra ", "siostra"),
            PersonRelative.of("up. siostra", "siostra"),
            PersonRelative.of("st.", "siostra"),
            PersonRelative.of("mt.", "matka"),
            PersonRelative.of("mt-", "matka"),
            PersonRelative.of("up. matka", "matka"),
            PersonRelative.of("br.", "brat"),
            PersonRelative.of("oj.", "ojciec"),
            PersonRelative.of("up. o.", "ojciec"),
            PersonRelative.of("narz.", "narzeczony(a)"),
            PersonRelative.of("up. narzcz.", "narzeczony(a)"),
            PersonRelative.of("up.", "mąż", "mąż"), //prefix-suffix pattern
            PersonRelative.of("partnerka", "partnerka"),
            PersonRelative.of("partner", "partner")
        );
    // TODO handle other relations
    // TODO handle two relatives ->
    //  "c.Małgorzata,c.Katarzyna"
    //  "c.Anna s.Andrzej"
    //  "c.Anna s,Zygmunt s.Andrze"


    private static final Predicate<String> isAnyRelation = guess -> RELATIVE_TYPES.stream().anyMatch(relativeType -> relativeType.isRelation().test(guess));

    /**
     * {@code true} remove matched lines
     */
    private final boolean removeGuesses;

    public AuthorizedPersonDeducer(IssuesLogger.ImportIssues issues, boolean removeGuesses) {
        super(issues);
        this.removeGuesses = removeGuesses;
    }

    @Override
    public Optional<List<Person.AuthorizedPerson>> deduceFrom(List<String> guesses) {
        final List<Person.AuthorizedPerson> authorizedPeople = deduce(guesses);

        if (!authorizedPeople.isEmpty()) {
            removeGuesses(guesses);
            return Optional.of(authorizedPeople);
        }
        return Optional.empty();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        if (this.removeGuesses) {
            guesses.removeIf(isAnyRelation);
        }
        return guesses;
    }

    private List<Person.AuthorizedPerson> deduce(List<String> guesses) {
        return RELATIVE_TYPES.stream()
                .map(relativeType -> relativeType.deduceAuthorizedPerson(guesses))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }


    public record PersonRelative(String prefix, String relation, String suffix) {
        static PersonRelative of(String distinct, String relation) {
            return new PersonRelative(distinct, relation, null);
        }

        static PersonRelative of(String prefix, String relation, String suffix) {
            return new PersonRelative(prefix, relation, suffix);
        }

        Predicate<String> isRelation() {
            if (StringUtils.isEmpty(suffix)) {
                return s -> StringUtils.isNotBlank(s) && s.startsWith(this.prefix);
            } else {
                return s -> StringUtils.isNotBlank(s) && s.startsWith(this.prefix) && s.endsWith(this.suffix);
            }
        }

        Optional<Person.AuthorizedPerson> deduceAuthorizedPerson(List<String> guesses) {
            final Optional<String> maybeRelation = guesses.stream()
                    .filter(isRelation())
                    .findFirst();
            return maybeRelation.map(goodGuess -> {
                        final String trimmed = StringUtils.removeStart(goodGuess, this.prefix).trim();
                        return trimmed.split(" ");
                    })
                    .filter(afterSplit -> afterSplit.length > 1)
                    .map(afterSplit -> new Person.AuthorizedPerson(afterSplit[0], afterSplit[1], this.relation, null));

        }

    }

}
