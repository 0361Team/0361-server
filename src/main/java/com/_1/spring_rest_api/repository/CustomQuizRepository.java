package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.CustomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomQuizRepository extends JpaRepository<CustomQuiz, Long> {
}
