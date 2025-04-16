package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.UserKakao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserKakaoRepository extends JpaRepository<UserKakao, Long> {
    Optional<UserKakao> findByKakaoAccountId(String kakaoAccountId);
}