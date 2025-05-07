package com._1.spring_rest_api.entity;


import com._1.spring_rest_api.api.dto.CourseResponse;
import com._1.spring_rest_api.api.dto.WeekResponse;
import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "COURSE")
@Getter
@Builder(toBuilder = true)
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

    @Setter
    @OneToMany(mappedBy = "course")
    private List<Week> weeks = new ArrayList<>();

    public Course(User creator, String title, String description) {
        this.creator = creator;
        this.title = title;
        this.description = description;
    }

    public Course(Long id, User creator, String title, String description) {
        this.id = id;
        this.creator = creator;
        this.title = title;
        this.description = description;
    }

    public CourseResponse toCourseResponse() {
        List<WeekResponse> weekResponses = weeks.stream()
                .map(Week::toWeekResponse)
                .collect(Collectors.toList());
        return new CourseResponse(id, title, description, weekResponses);
    }
}