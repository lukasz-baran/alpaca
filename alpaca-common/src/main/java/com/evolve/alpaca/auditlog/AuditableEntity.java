package com.evolve.alpaca.auditlog;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface AuditableEntity {

    @JsonIgnore
    String getEntityId();

    @JsonIgnore
    Class<?> getAuditableClass();

}
