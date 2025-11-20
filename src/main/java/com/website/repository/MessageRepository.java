package com.website.repository;

import com.website.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findTop10ByUserCodeAndConvIdOrderByCreatedAtDesc(Long userCode, ObjectId convId);


    Page<Message> findByConvIdAndUserCode(ObjectId id, Long userCode, Pageable pageable);
}
