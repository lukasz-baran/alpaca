package com.evolve.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PersonLookupCriteria {
    public static final long DEFAULT_PAGE = 1;
    public static final long DEFAULT_PAGE_SIZE = 50;

    @Builder.Default
    private Long page = DEFAULT_PAGE;
    @Builder.Default
    private Long pageSize = DEFAULT_PAGE_SIZE;

    private String sortBy;
    private Boolean upDown;
}
