package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WEEK")
@Getter
@Builder
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
    private List<Text> texts = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    private List<SupplementalFile> supplementalFiles = new ArrayList<>();

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL)
    private List<QuizWeekMapping> quizWeekMappings = new ArrayList<>();

    public Week(Long id, String title, Course course) {
        this.id = id;
        this.title = title;
        this.course = course;
    }

    public WeekResponse toWeekResponse() {
        return new WeekResponse(id, course.toCourseResponse(), title);
    }
}