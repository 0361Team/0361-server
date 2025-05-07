package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "QUESTION")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    @Column(name = "front", columnDefinition = "TEXT")
    private String front;

    @Column(name = "back", columnDefinition = "TEXT")
    private String back;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<QuizQuestionMapping> quizQuestionMappings = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    // DTO로 변환하는 메서드
    public QuestionResponse toQuestionResponse() {
        return QuestionResponse.builder()
                .id(this.id)
                .weekId(this.week != null ? this.week.getId() : null)
                .front(this.front) // 필드명 변경
                .back(this.back) // 필드명 변경
                .build();
    }
}