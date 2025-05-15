package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.QuizQuestionMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionMappingRepository extends JpaRepository<QuizQuestionMapping, Long> {
}
