package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.QuizWeekMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizWeekMappingRepository extends JpaRepository <QuizWeekMapping, Long> {
}
