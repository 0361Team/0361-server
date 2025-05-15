package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
}
