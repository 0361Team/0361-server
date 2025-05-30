package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.CustomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CustomQuizRepository extends JpaRepository<CustomQuiz, Long> {

    @Query("SELECT q FROM CustomQuiz q WHERE q.creator.id = :creatorId ORDER BY q.createAt DESC")
    List<CustomQuiz> findAllByCreatorId(@Param("creatorId") Long creatorId);

    @Query("SELECT DISTINCT q FROM CustomQuiz q " +
            "LEFT JOIN FETCH q.creator " +
            "LEFT JOIN FETCH q.quizQuestionMappings qm " +
            "LEFT JOIN FETCH qm.question " +
            "WHERE q.id = :quizId")
    Optional<CustomQuiz> findByIdWithQuestions(@Param("quizId") Long quizId);

    @Query("SELECT DISTINCT q FROM CustomQuiz q " +
            "LEFT JOIN FETCH q.quizWeekMappings wm " +
            "LEFT JOIN FETCH wm.week w " +
            "LEFT JOIN FETCH w.course " +
            "WHERE q.id = :quizId")
    Optional<CustomQuiz> findByIdWithWeeks(@Param("quizId") Long quizId);

}
