package com.evolve.repo.jpa;

import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<Person> findByCriteria(PersonLookupCriteria criteria) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        final Root<Person> person = cq.from(Person.class);

        final Set<Predicate> predicates = new HashSet<>();
        if (StringUtils.isNotEmpty(criteria.getUnitNumber())) {
            predicates.add(cb.equal(person.get("unitNumber"), criteria.getUnitNumber()));
        }

        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(person.get("status"), criteria.getStatus()));
        }

        if (criteria.getGender() != null) {
            predicates.add(cb.equal(person.get("gender"), criteria.getGender()));
        }

        if (criteria.getRetired() != null) {
            if (criteria.getRetired()) {
                predicates.add(cb.isTrue(person.get("retired")));
            } else {
                predicates.add(
                        cb.or(
                                cb.isNull(person.get("retired")),
                                cb.isFalse(person.get("retired"))));
            }
        }

        if (criteria.getExemptFromFees() != null) {
            if (criteria.getExemptFromFees()) {
                predicates.add(cb.isTrue(person.get("exemptFromFees")));
            } else {
                predicates.add(
                        cb.or(
                            cb.isNull(person.get("exemptFromFees")),
                            cb.isFalse(person.get("exemptFromFees"))));
            }
        }

        if (criteria.getRegistryNumber() != null) {
            predicates.add(cb.equal(person.get("registryNumber").get("registryNum"), criteria.getRegistryNumber()));
        }

        final Order order = criteria.getOrder(person, cb);
        cq.where(predicates.toArray(new Predicate[]{})).orderBy(order);

        return em.createQuery(cq).getResultList();
    }
}
