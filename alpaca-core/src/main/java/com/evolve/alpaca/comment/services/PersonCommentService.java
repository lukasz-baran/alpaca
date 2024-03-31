package com.evolve.alpaca.comment.services;

import com.evolve.alpaca.comment.Comment;
import com.evolve.alpaca.comment.repo.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonCommentService {
    private final CommentRepository commentRepository;

    public List<Comment> findPersonComments(String personId) {

        // TODO add sorting by dateAdded
        return commentRepository.findByPersonId(personId);
    }

}
