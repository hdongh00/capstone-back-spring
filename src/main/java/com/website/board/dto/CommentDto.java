package com.website.board.dto;

import com.website.board.domain.Comment;
import lombok.Data;
import java.time.LocalDateTime;

public class CommentDto {

    @Data
    public static class CreateRequest {
        private String content;
        private Long parentCommentId;
    }

    @Data
    public static class Response {
        private Long id;
        private Long forumId;
        private Long userCode;
        private String content;
        private Long parentCommentId;
        private boolean deleted;
        private LocalDateTime createdAt;

        public static Response from(Comment comment) {
            Response res = new Response();
            res.setId(comment.getId());
            res.setForumId(comment.getForumId());
            res.setUserCode(comment.getUserCode());
            res.setContent(comment.getContent());
            res.setParentCommentId(comment.getParentCommentId());
            res.setDeleted(comment.isDeleted());
            res.setCreatedAt(comment.getCreatedAt());
            return res;
        }
    }
}
