package com.evolve.alpaca.comment;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    private String personId;

    private String comment;

    private LocalDateTime added;

    public Comment(String personId, String comment, LocalDateTime added) {
        this.personId = personId;
        this.comment = comment;
        this.added = added;
    }
}
