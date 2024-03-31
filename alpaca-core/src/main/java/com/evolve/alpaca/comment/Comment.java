package com.evolve.alpaca.comment;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;


/**
 *
 * TODO Comments about persons should be implemented as a separate entity
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Comment {
    @Id
    private Long commentId;

    private String personId;

    private String comment;

    private LocalDateTime added;

}
