package com.evolve.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class Comment {
    private String comment;
    private LocalDateTime added;

}
