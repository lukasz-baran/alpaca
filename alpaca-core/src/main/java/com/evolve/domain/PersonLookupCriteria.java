package com.evolve.domain;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.BooleanUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

@Builder
@Getter
public class PersonLookupCriteria {
    public static final long DEFAULT_PAGE = 1;
    public static final long DEFAULT_PAGE_SIZE = 50;
    public static final PersonLookupCriteria ALL = PersonLookupCriteria.builder().build();

    @Builder.Default
    private Long page = DEFAULT_PAGE;
    @Builder.Default
    private Long pageSize = DEFAULT_PAGE_SIZE;

    private String sortBy;
    private Boolean upDown;

    private String unitNumber;
    private Boolean hasDocuments;
    private PersonStatus status;
    private Person.Gender gender;

    private Boolean retired;
    private Boolean exemptFromFees;

    private Integer registryNumber;

    public Order getOrder(Root<Person> root, CriteriaBuilder cb) {
        final String orderField = sortBy == null || "id".equals(sortBy) ? "personId" : sortBy;
        return BooleanUtils.isTrue(upDown) ?
                cb.asc(root.get(orderField))
                : cb.desc(root.get(orderField));
    }
}
