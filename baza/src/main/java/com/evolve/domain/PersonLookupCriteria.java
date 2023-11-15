package com.evolve.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.Sort;

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

    private String unitNumber;

    @JsonIgnore
    public Sort getSort() {
        final Sort sort = Sort.by(sortBy == null || "id".equals(sortBy) ? "personId" : sortBy);
        return BooleanUtils.isTrue(upDown) ? sort.ascending() : sort.descending();
    }
}
