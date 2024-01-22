package com.evolve.repo.jpa;

import com.evolve.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByPersonId(String personId);

    List<Account> findByAccountTypeInOrUnitNumberIn(Collection<Account.AccountType> types,
                                                    Collection<String> unitNumber);
}
