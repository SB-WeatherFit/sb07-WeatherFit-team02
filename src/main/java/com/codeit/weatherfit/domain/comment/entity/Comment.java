package com.codeit.weatherfit.domain.comment.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.feed.entity.Feed;
import com.codeit.weatherfit.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
public class Comment extends BaseEntity {

    @ManyToOne
    private User author;
    @ManyToOne
    private Feed feed;
    private String content;

}
