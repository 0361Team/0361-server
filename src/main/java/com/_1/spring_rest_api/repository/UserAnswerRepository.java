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

    /**
     * 특정 사용자의 모든 답변을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 답변 목록
     */
    @Query("SELECT a FROM UserAnswer a WHERE a.user.id = :userId ORDER BY a.answeredAt DESC")
    List<UserAnswer> findAllByUserId(@Param("userId") Long userId);


    @Query("SELECT ua FROM UserAnswer ua WHERE ua.user.id = :userId AND ua.question.id IN :questionIds")
    List<UserAnswer> findByUserIdAndQuestionIdIn(
            @Param("userId") Long userId,
            @Param("questionIds") Collection<Long> questionIds);
}