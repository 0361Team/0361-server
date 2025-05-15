package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.CustomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CustomQuizRepository extends JpaRepository<CustomQuiz, Long> {

    @Query("SELECT q FROM CustomQuiz q WHERE q.creator.id = :creatorId ORDER BY q.createAt DESC")
    List<CustomQuiz> findAllByCreatorId(@Param("creatorId") Long creatorId);

}
