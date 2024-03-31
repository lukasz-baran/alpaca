package com.evolve.alpaca.gui.comments;

import com.evolve.alpaca.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PersonCommentEntry {
    private Long id;

    private final LocalDateTime dateAdded;
    private String content;

    public static PersonCommentEntry of(Comment comment) {
        return new PersonCommentEntry(comment.getCommentId(), comment.getAdded(), comment.getComment());
    }

}
