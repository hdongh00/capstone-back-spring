package com.website.board.repository;

import com.website.board.domain.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    List<Forum> findAllByIsDeletedFalseOrderByCreatedAtDesc();
    List<Forum> findAllByIsDeletedFalse();
    List<Forum> findAllByUserCode(Long userCode);
}
