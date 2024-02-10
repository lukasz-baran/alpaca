package com.evolve.repo.jpa;

import com.evolve.domain.Person;
import com.evolve.domain.PersonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, PersonId> {

    Person findByPersonId(String personId);

    @Query("SELECT p FROM Person p where SUBSTRING(p.personId, 0, 2) = :groupNumber")
    List<Person> findByGroupName(@Param("groupNumber") String groupNumber);

    @Query(value = "SELECT MAX(registryNumber.registryNum) FROM Person")
    Integer findMaxRegistryNumber();
}
