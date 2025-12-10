package com.website.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_code")
    private Long id;

    @Column(name = "forum_code", nullable = false)
    private Long forumId;

    @Column(name = "user_code", nullable = false)
    private Long userCode;

    @Column(name = "content", columnDefinition = "VARCHAR(1000)")
    private String content;

    @Column(name = "is_delete")
    private boolean isDeleted;

    @Column(name = "comment_code_ref")
    private Long parentCommentId;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Comment(Long forumId, Long userCode, String content, Long parentCommentId) {
        this.forumId = forumId;
        this.userCode = userCode;
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
