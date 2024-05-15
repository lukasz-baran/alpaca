package com.evolve.alpaca.auditlog;

import java.util.List;

public interface FindAuditLog {

    List<AuditEntry> findById(Class<?> clazz, String entityId);
}
