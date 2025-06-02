package com._1.spring_rest_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Keyword")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "TEXT_id")
    private Text text;

    public void changeText(Text text) {
        this.text = text;
        if (text != null && !text.getKeywords().contains(this)) {
            text.getKeywords().add(this);
        }
    }
}
