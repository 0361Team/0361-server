package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.Keyword;
import com._1.spring_rest_api.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findAllByText(Text text);
}
