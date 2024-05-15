package com.evolve.alpaca.auditlog;

public interface AuditableEntity {

    String getEntityId();

    Class<?> getAuditableClass();

}
