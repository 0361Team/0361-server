package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WEEK")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Week extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "title")
    private String title;

    @Column(name = "week_number")
    private Integer weekNumber;

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Text> texts = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SupplementalFile> supplementalFiles = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizWeekMapping> quizWeekMappings = new ArrayList<>();

    // Course와 Week 간의 양방향 연관관계 메서드
    public void changeCourse(Course course) {
        this.course = course;
        if (course != null && !course.getWeeks().contains(this)) {
            course.getWeeks().add(this);
        }
    }

    // Week와 Question 간의 양방향 연관관계 메서드
    public void addQuestion(Question question) {
        this.questions.add(question);
        if (question.getWeek() != this) {
            question.changeWeek(this);
        }
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
        if (question.getWeek() == this) {
            question.changeWeek(null);
        }
    }

    // Week와 Text 간의 양방향 연관관계 메서드
    public void addText(Text text) {
        this.texts.add(text);
        if (text.getWeek() != this) {
            text.changeWeek(this);
        }
    }

    public void removeText(Text text) {
        this.texts.remove(text);
        if (text.getWeek() == this) {
            text.changeWeek(null);
        }
    }

    // 4. Week와 SupplementalFile 간의 양방향 연관관계 메서드
    public void addSupplementalFile(SupplementalFile file) {
        this.supplementalFiles.add(file);
        if (file.getWeek() != this) {
            file.changeWeek(this);
        }
    }

    public void removeSupplementalFile(SupplementalFile file) {
        this.supplementalFiles.remove(file);
        if (file.getWeek() == this) {
            file.changeWeek(null);
        }
    }

    // Week와 QuizWeekMapping 간의 양방향 연관관계 메서드
    public void addQuizWeekMapping(QuizWeekMapping mapping) {
        this.quizWeekMappings.add(mapping);
        if (mapping.getWeek() != this) {
            mapping.changeWeek(this);
        }
    }

    public void removeQuizWeekMapping(QuizWeekMapping mapping) {
        this.quizWeekMappings.remove(mapping);
        if (mapping.getWeek() == this) {
            mapping.changeWeek(null);
        }
    }

    public Week(Long id, String title, Course course) {
        this.id = id;
        this.title = title;
        this.course = course;
    }

    public WeekResponse toWeekResponse() {
        return new WeekResponse(id, course.toCourseResponse(), title);
    }
}