package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {
}
