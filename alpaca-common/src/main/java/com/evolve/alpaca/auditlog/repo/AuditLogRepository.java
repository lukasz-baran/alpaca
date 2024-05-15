package com.evolve.alpaca.auditlog.repo;

import com.evolve.alpaca.auditlog.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditEntry, Long> {

    @Query("SELECT entry FROM AuditEntry entry WHERE entry.entityId = :entityId AND entry.clazz = :clazz")
    List<AuditEntry> findByEntityIdAndClazz(@Param("entityId") String entityId, @Param("clazz") String clazz);

}
