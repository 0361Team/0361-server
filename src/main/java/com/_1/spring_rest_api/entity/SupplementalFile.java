package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "SUPPLEMENTAL_FILE")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SupplementalFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_type")
    private String fileType;

    public void changeWeek(Week week) {
        this.week = week;
        if (week != null && !week.getSupplementalFiles().contains(this)) {
            week.getSupplementalFiles().add(this);
        }
    }
}