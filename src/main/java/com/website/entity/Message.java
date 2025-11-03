package com.website.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@CompoundIndex(name = "conv_create_idx", def = "{'convId': 1, 'createdAt': 1}")
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class Message {
    @Id
    private ObjectId id;
    private ObjectId convId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private String role;
    private Long userCode;
}
