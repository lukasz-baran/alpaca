package com.evolve.alpaca.auditlog.services;

import com.evolve.alpaca.auditlog.AuditEntry;
import com.evolve.alpaca.auditlog.FindAuditLog;
import com.evolve.alpaca.auditlog.repo.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuditLogQueryService implements FindAuditLog {

    private final AuditLogRepository auditLogRepository;

    @Override
    public List<AuditEntry> findById(Class<?> clazz, String entityId) {
        final String clazzName = clazz.getName();

        return auditLogRepository.findByEntityIdAndClazz(entityId, clazzName);
    }
}
