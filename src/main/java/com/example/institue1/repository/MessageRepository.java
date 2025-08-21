package com.example.institue1.repository;

import com.example.institue1.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageRepository extends JpaRepository<Message, Long> , JpaSpecificationExecutor {
}
