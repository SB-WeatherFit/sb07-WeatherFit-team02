package com.codeit.weatherfit.domain.notification.service;

import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationDto;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.repository.NotificationRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    NotificationService notificationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    EntityManager em;


    @Test
    void send() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);
        em.flush();
        em.clear();

        NotificationDto result
                = notificationService.send(saved.getId(), "title", "content", NotificationLevel.INFO);

        assertThat(result.id()).isNotNull();
        ;
        assertThat(result.receiverId()).isEqualTo(saved.getId());
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.content()).isEqualTo("content");
    }

    @Test
    void broadcast() {
        Set<UUID> targetUserIds = new HashSet<>();
        UUID firstUserId = null;
        for (int i = 0; i < 10; i++) {
            User user = UserFixture.createUser("test@gmail.com" + i);
            User saved = userRepository.save(user);
            targetUserIds.add(saved.getId());
            if (i == 0) {
                firstUserId = saved.getId();
            }
        }
        em.flush();
        em.clear();

        List<NotificationDto> result = notificationService.broadcast("title", "content", NotificationLevel.INFO, targetUserIds);

        assertThat(result.size()).isEqualTo(10);
        assertThat(result.getFirst().receiverId()).isEqualTo(firstUserId);
    }

    @Test
    void search() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);

        for (int i = 0; i < 40; i++) {
            Notification notification = Notification.create(user, "title" + i, "content", NotificationLevel.INFO);
            notificationRepository.save(notification);
        }

        em.flush();
        em.clear();

        NotificationSearchCondition condition = new NotificationSearchCondition(null, null, 20);
        NotificationCursorResponse result = notificationService.search(condition, saved.getId());

        assertThat(result.data().size()).isEqualTo(20);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();
        assertThat(result.nextIdAfter()).isNotNull();
        assertThat(result.totalCount()).isEqualTo(40);

        NotificationSearchCondition condition2 = new NotificationSearchCondition(result.nextCursor(), result.nextIdAfter(), 20);
        NotificationCursorResponse result2 = notificationService.search(condition2, saved.getId());

        assertThat(result2.data().size()).isEqualTo(20);
        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.nextCursor()).isNull();
        assertThat(result2.nextIdAfter()).isNull();
        assertThat(result2.totalCount()).isEqualTo(40);
        assertThat(result2.data().getFirst().createdAt()).isAfter(result2.data().getLast().createdAt());
    }

    @Test
    void delete() {
        User user = UserFixture.createUser();
        userRepository.save(user);

        Notification notification = Notification.create(user, "title", "content", NotificationLevel.INFO);
        Notification save = notificationRepository.save(notification);

        em.flush();
        em.clear();

        notificationService.delete(save.getId());

        em.flush();
        em.clear();

        assertThat(notificationRepository.findById(save.getId()).isPresent()).isFalse();
    }
}