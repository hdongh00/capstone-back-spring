package com.website.board.repository;

import com.website.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByForumIdOrderByCreatedAtAsc(Long forumId);
    List<Comment> findAllByForumIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long forumId);
    long countByForumId(Long forumId);
}
