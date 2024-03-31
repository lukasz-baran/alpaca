package com.evolve.alpaca.comment.repo;

import com.evolve.alpaca.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPersonId(String personId);

}
