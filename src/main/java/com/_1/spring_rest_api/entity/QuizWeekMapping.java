package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "QUIZ_WEEK_MAPPING")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QuizWeekMapping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private CustomQuiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    // CustomQuiz와 QuizWeekMapping 간의 양방향 연관관계 메서드
    public void changeQuiz(CustomQuiz quiz) {
        this.quiz = quiz;
        if (quiz != null && !quiz.getQuizWeekMappings().contains(this)) {
            quiz.getQuizWeekMappings().add(this);
        }
    }

    // Week와 QuizWeekMapping 간의 양방향 연관관계 메서드
    public void changeWeek(Week week) {
        this.week = week;
        if (week != null && !week.getQuizWeekMappings().contains(this)) {
            week.getQuizWeekMappings().add(this);
        }
    }
}