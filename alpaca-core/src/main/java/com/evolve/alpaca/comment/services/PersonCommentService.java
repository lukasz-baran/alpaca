package com.evolve.alpaca.comment.services;

import com.evolve.alpaca.comment.Comment;
import com.evolve.alpaca.comment.repo.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonCommentService {
    private final CommentRepository commentRepository;

    public List<Comment> findPersonComments(String personId) {

        // TODO add sorting by dateAdded
        return commentRepository.findByPersonId(personId);
    }

    public Comment addNewComment(String personId, String content) {
        return commentRepository.save(new Comment(personId, content, LocalDateTime.now()));
    }

    public Optional<Comment> editComment(Long id, String content) {
        return commentRepository.findById(id)
                .flatMap(entry -> {
                    entry.setComment(content);
                    return Optional.of(commentRepository.save(entry));
                });
    }

    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }
}
