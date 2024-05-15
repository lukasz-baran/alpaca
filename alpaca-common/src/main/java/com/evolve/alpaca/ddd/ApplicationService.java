package com.evolve.alpaca.ddd;

import com.evolve.alpaca.auditlog.AuditEntry;
import com.evolve.alpaca.auditlog.AuditableEntity;
import com.evolve.alpaca.auditlog.repo.AuditLogRepository;
import com.evolve.alpaca.utils.LogUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
public abstract class ApplicationService<ENTITY extends AuditableEntity> {
    private final ObjectMapper objectMapper = LogUtil.OBJECT_MAPPER;

    protected final CommandCollector commandCollector;
    private final AuditLogRepository auditLogRepository;

    public <COMMAND extends Command> PersistedCommand persistCommand(ENTITY original, ENTITY changed,
                                                                     COMMAND command, LocalDateTime when) {

        final String originalValue = serializeObject(original);
        final String changedValue = serializeObject(changed);

        final PersistedCommand persistedCommand = commandCollector.addCommand(command);

        if (originalValue != null && changedValue != null) {

            if (!StringUtils.equals(original.getEntityId(), changed.getEntityId())) {
                log.error("Different IDs {} and {}", original.getEntityId(), changed.getEntityId());
            } else {

                final AuditEntry auditEntry = auditLogRepository.save(new AuditEntry(original.getEntityId(), originalValue,
                        changedValue, command.getClass().getName(), when));

                log.info("Audit entry saved: {}", auditEntry);
            }
        } else {
            log.warn("Unable to create audit log entry");
        }

        return persistedCommand;
    }

    private String serializeObject(ENTITY entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            log.warn("Unable to serialize {}", entity);
            return null;
        }
    }

}
