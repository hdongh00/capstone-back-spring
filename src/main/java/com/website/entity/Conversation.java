package com.website.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("conversations")
@AllArgsConstructor
@NoArgsConstructor@Getter@Setter
public class Conversation {
    @Id
    private ObjectId _id;
    private Long userCode;
    private LocalDateTime date;
    private String ai;
    private String status;

    public Conversation(Long userCode, LocalDateTime date, String ai, String status) {
        this.userCode = userCode;
        this.date = date;
        this.ai = ai;
        this.status = status;
    }
}
