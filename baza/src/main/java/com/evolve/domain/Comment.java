package com.evolve.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private final String comment;
    private final LocalDateTime added;

}
