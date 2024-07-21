package com.evolve.alpaca.auditlog;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@ToString
public class AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long auditEntryId;

    private String entityId;

    @Column(columnDefinition = "text")
    private String before;

    @Column(columnDefinition = "text")
    private String after;

    private String clazz;

    private LocalDateTime when;

    public AuditEntry(String entityId, String before, String after, String clazz, LocalDateTime when) {
        this.entityId = entityId;
        this.before = before;
        this.after = after;
        this.clazz = clazz;
        this.when = when;
    }
}
