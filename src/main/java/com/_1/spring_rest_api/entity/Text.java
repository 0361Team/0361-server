package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TEXT")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Text extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Setter
    @Column(name = "summation", columnDefinition = "TEXT")
    private String summation;

    @Column(name = "type")
    private String type;

    // Week와 Text 간의 양방향 연관관계 메서드
    public void changeWeek(Week week) {
        this.week = week;
        if (week != null && !week.getTexts().contains(this)) {
            week.getTexts().add(this);
        }
    }

    // 컨텐츠 업데이트 메서드
    public void updateContent(String content) {
        this.content = content;
    }

    // 타입 업데이트 메서드
    public void updateType(String type) {
        this.type = type;
    }
}