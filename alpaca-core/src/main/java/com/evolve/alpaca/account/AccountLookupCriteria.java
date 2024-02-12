package com.evolve.alpaca.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.Sort;

@Builder
public class AccountLookupCriteria {
    public static final long DEFAULT_PAGE = 1;
    public static final long DEFAULT_PAGE_SIZE = 50;
    public static final AccountLookupCriteria ALL = AccountLookupCriteria.builder().build();

    @Builder.Default
    private Long page = DEFAULT_PAGE;
    @Builder.Default
    private Long pageSize = DEFAULT_PAGE_SIZE;

    private String sortBy;
    @Builder.Default
    private Boolean upDown = true;

    @JsonIgnore
    public Sort getSort() {
        final Sort sort = Sort.by(sortBy == null || "id".equals(sortBy) ? "accountId" : sortBy);
        return BooleanUtils.isTrue(upDown) ? sort.ascending() : sort.descending();
    }
}
