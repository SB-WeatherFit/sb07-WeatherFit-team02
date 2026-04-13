package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_DEFAULT) // TODO default 유저 세팅
    private User author;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Feed feed;

    private String content;

    public static Comment create(User author, Feed feed, String content) {
        Comment comment = new Comment();
        comment.author = author;
        comment.feed = feed;
        comment.content = content;
        return comment;
    }
}
