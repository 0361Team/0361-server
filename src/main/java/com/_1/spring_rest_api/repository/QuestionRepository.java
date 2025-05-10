package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.week.id = :weekId")
    List<Question> findAllByWeekId(@Param("weekId") Long weekId);
}
