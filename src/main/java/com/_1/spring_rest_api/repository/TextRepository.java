package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextRepository extends JpaRepository<Text, Long> {

    List<Text> findAllByWeekId(Long weekId);
}
