package com._1.spring_rest_api.entity;


import com._1.spring_rest_api.api.dto.CourseResponse;
import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "COURSE")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "course")
    @Builder.Default
    private List<Week> weeks = new ArrayList<>();

    // Course와 Week 간의 양방향 연관관계 메서드
    public void addWeek(Week week) {
        this.weeks.add(week);
        if (week.getCourse() != this) {
            week.changeCourse(this);
        }
    }

    public void removeWeek(Week week) {
        this.weeks.remove(week);
        if (week.getCourse() == this) {
            week.changeCourse(null);
        }
    }

    // User와 Course 간의 양방향 연관관계 메서드
    public void changeCreator(User creator) {
        this.creator = creator;
        if (creator != null && !creator.getCourses().contains(this)) {
            creator.getCourses().add(this);
        }
    }


    public Course(User creator, String title, String description) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.weeks = new ArrayList<>();
    }

    public Course(Long id, User creator, String title, String description) {
        this.id = id;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.weeks = new ArrayList<>();
    }

    public CourseResponse toCourseResponse() {
        List<WeekResponse> weekResponses = weeks.stream()
                .map(Week::toWeekResponse)
                .collect(Collectors.toList());
        return new CourseResponse(id, title, description, weekResponses);
    }
}