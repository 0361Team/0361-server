package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {

    /**
     * 특정 사용자의 모든 퀴즈 세션을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 퀴즈 세션 목록
     */
    @Query("SELECT s FROM QuizSession s WHERE s.user.id = :userId ORDER BY s.createAt DESC")
    List<QuizSession> findAllByUserId(@Param("userId") Long userId);

    /**
     * 특정 퀴즈의 모든 세션을 조회합니다.
     *
     * @param quizId 퀴즈 ID
     * @return 퀴즈 세션 목록
     */
    @Query("SELECT s FROM QuizSession s WHERE s.quiz.id = :quizId ORDER BY s.createAt DESC")
    List<QuizSession> findAllByQuizId(@Param("quizId") Long quizId);

    /**
     * 특정 사용자가 특정 퀴즈에 대해 생성한 모든 세션을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param quizId 퀴즈 ID
     * @return 퀴즈 세션 목록
     */
    @Query("SELECT s FROM QuizSession s WHERE s.user.id = :userId AND s.quiz.id = :quizId ORDER BY s.createAt DESC")
    List<QuizSession> findAllByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * 특정 사용자의 완료된 세션만 조회합니다.
     *
     * @param userId 사용자 ID
     * @param completed 완료 여부
     * @return 퀴즈 세션 목록
     */
    @Query("SELECT s FROM QuizSession s WHERE s.user.id = :userId AND s.completed = :completed ORDER BY s.createAt DESC")
    List<QuizSession> findAllByUserIdAndCompleted(@Param("userId") Long userId, @Param("completed") Boolean completed);
}