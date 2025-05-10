package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "USER_AGREE")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserAgree extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_agreed")
    private Boolean isAgreed;

    // Term과 UserAgree 간의 양방향 연관관계 메서드
    public void changeTerm(Term term) {
        this.term = term;
        if (term != null && !term.getUserAgrees().contains(this)) {
            term.getUserAgrees().add(this);
        }
    }

    // User와 UserAgree 간의 양방향 연관관계 메서드
    public void changeUser(User user) {
        this.user = user;
        if (user != null && !user.getUserAgrees().contains(this)) {
            user.getUserAgrees().add(this);
        }
    }
}