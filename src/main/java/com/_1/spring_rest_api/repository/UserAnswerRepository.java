package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    @Query("SELECT ua FROM UserAnswer ua WHERE ua.quizSession.id = :sessionId ORDER BY ua.answeredAt ASC")
    List<UserAnswer> findByQuizSessionIdOrderByAnsweredAt(@Param("sessionId") Long sessionId);
}