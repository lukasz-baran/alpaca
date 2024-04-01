package com.evolve.alpaca.search;

import com.evolve.FindPerson;
import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.account.services.AccountsService;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PersonSearchService {
    private final FindPerson findPerson;
    private final AccountsService accountsService;


    public List<PersonListView> defaultOrder(PersonSearchCriteria criteria) {
        return search("id", true, criteria);
    }

    private List<PersonListView> search(String sortBy, boolean upDown, PersonSearchCriteria criteria) {
        List<PersonListView> persons = findPerson.fetchList(
                PersonLookupCriteria.builder()
                        .sortBy(sortBy)
                        .upDown(upDown)
                        .unitNumber(criteria.unitNumber())
                        .hasDocuments(criteria.hasDocuments())
                        .status(criteria.personStatus())
                        .gender(criteria.personGender())
                        .retired(criteria.isRetired())
                        .exemptFromFees(criteria.isExemptFromFees())
                        .build());

        if (!criteria.hasAccountUnits().isEmpty() || !criteria.hasAccountTypes().isEmpty()) {
            Set<String> personIds = accountsService.findByUnitAndAccountType(criteria.hasAccountTypes(), criteria.hasAccountUnits())
                    .stream()
                    .map(Account::getPersonId)
                    .collect(Collectors.toSet());

            return persons.stream()
                    .filter(person -> personIds.contains(person.personId()))
                    .toList();
        }
        return persons;
    }


}
