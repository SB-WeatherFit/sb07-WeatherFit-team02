package com.codeit.weatherfit.domain.notification.repository;

import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.config.JpaAuditingConfig;
import com.codeit.weatherfit.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
class NotificationCustomRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void searchCursor() {
        User user = UserFixture.createUser();
        User saved = userRepository.save(user);

        for (int i = 0; i < 40; i++) {
            Notification notification = Notification.create(user, "title" + i, "content", NotificationLevel.INFO);
            Notification save = notificationRepository.save(notification);
            jdbcTemplate.update(
                    "UPDATE notifications SET created_at = ? WHERE id = ?",
                    Timestamp.from(Instant.now().minusSeconds(i * 60)),
                    save.getId()
            );
        }

        em.flush();
        em.clear();

        NotificationSearchCondition condition = new NotificationSearchCondition(null, null, 20);
        List<Notification> notifications = notificationRepository.searchCursor(condition);

        assertThat(notifications.size()).isEqualTo(21);
        assertThat(notifications).allSatisfy(n ->
                assertThat(n.getReceiver().getId()).isEqualTo(saved.getId())
        );
        assertThat(notifications.getFirst().getTitle()).isEqualTo("title" + 39);
        assertThat(notifications.getFirst().getCreatedAt()).isAfter(notifications.getLast().getCreatedAt());

        NotificationSearchCondition condition2 = new NotificationSearchCondition(notifications.get(19).getCreatedAt(), notifications.get(19).getId(), 20);
        List<Notification> notifications2 = notificationRepository.searchCursor(condition2);

        assertThat(notifications2.size()).isEqualTo(20);
        assertThat(notifications2).allSatisfy(n ->
                assertThat(n.getReceiver().getId()).isEqualTo(saved.getId())
        );
        assertThat(notifications2.getFirst().getCreatedAt()).isAfter(notifications2.getLast().getCreatedAt());

        Notification lastOfFirstResult = notifications.get(19);
        Notification firstOfSecondResult = notifications2.getFirst();

        assertThat(lastOfFirstResult.getCreatedAt()).isAfter(firstOfSecondResult.getCreatedAt());

        assertThat(notifications2)
                .extracting(Notification::getTitle)
                .containsExactly(
                        "title19", "title18", "title17", "title16", "title15",
                        "title14", "title13", "title12", "title11", "title10",
                        "title9", "title8", "title7", "title6", "title5",
                        "title4", "title3", "title2", "title1", "title0"
                );
    }
}