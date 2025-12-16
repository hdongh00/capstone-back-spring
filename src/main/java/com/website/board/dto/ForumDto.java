package com.website.board.dto;

import lombok.Data;
import java.time.LocalDateTime;

public class ForumDto {

    @Data
    public static class CreateRequest {
        private String title;
        private String content;
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String content;
    }

    @Data
    public static class Response {
        private Long id;
        private Long userCode;
        private String title;
        private String content;
        private boolean deleted;
        private String analysisSummary;
        private String emotionName;
        private int commentCount;
        private int likeCount;
        private LocalDateTime createdAt;

        public static Response from(com.website.board.domain.Forum forum, long commentCount) {
            Response response = new Response();
            response.setId(forum.getId());
            response.setUserCode(forum.getUserCode());
            response.setTitle(forum.getTitle());
            response.setContent(forum.getContent());
            response.setDeleted(forum.isDeleted());
            response.setAnalysisSummary(null);
            response.setEmotionName(null);
            response.setCommentCount((int) commentCount);
            response.setLikeCount(
                    forum.getLikedUserCodes() != null
                            ? forum.getLikedUserCodes().size()
                            : 0
            );
            response.setCreatedAt(forum.getCreatedAt());
            return response;
        }
    }
}
