package com._1.spring_rest_api.entity;

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
@Table(name = "TERM")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Term extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_required")
    private Boolean isRequired;

    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAgree> userAgrees = new ArrayList<>();

    // Term과 UserAgree 간의 양방향 연관관계 메서드
    public void addUserAgree(UserAgree userAgree) {
        this.userAgrees.add(userAgree);
        if (userAgree.getTerm() != this) {
            userAgree.changeTerm(this);
        }
    }

    public void removeUserAgree(UserAgree userAgree) {
        this.userAgrees.remove(userAgree);
        if (userAgree.getTerm() == this) {
            userAgree.changeTerm(null);
        }
    }
}