package com.website.repository;

import com.website.entity.Conversation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, ObjectId> {

    List<Conversation> findAllByUserCodeAndDateBetween(Long userCode, LocalDateTime start, LocalDateTime end);

    List<Conversation> findAllByUserCodeAndStatusAndDateBetween(Long userCode, String  status, LocalDateTime start, LocalDateTime end);

    Integer countByUserCode(Long userCode);

    Integer countByUserCodeAndDateBetween(Long userCode, LocalDateTime start, LocalDateTime end);

    Boolean existsByUserCodeAndDateBetween(Long userCode, LocalDateTime start, LocalDateTime end);
}
