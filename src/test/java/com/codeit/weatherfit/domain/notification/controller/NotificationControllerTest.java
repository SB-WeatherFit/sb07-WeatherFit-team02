package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.repository.NotificationRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    MockMvcTester mvcTester;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    private KafkaTemplate<String, MessageCreatedEvent> kafkaTemplate;

    @Test
    void search() {
        User user = UserFixture.createUser();
        userRepository.save(user);

        for (int i = 0; i < 21; i++) {
            Notification notification = Notification.create(user, "title" + i, "content", NotificationLevel.INFO);
            notificationRepository.save(notification);
        }

        WeatherFitUserDetails details = WeatherFitUserDetails.from(user);

        NotificationSearchCondition condition = new NotificationSearchCondition(null, null, 20);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = objectMapper.convertValue(condition, new TypeReference<>() {});
        params.setAll(map);

        assertThat(
                mvcTester.get().uri("/api/notifications")
                        .with(csrf())
                        .with(user(details))
                        .queryParams(params)
        )
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .convertTo(NotificationCursorResponse.class)
                .satisfies(result-> {
                    assertThat(result.data().size()).isEqualTo(20);
                    assertThat(result.hasNext()).isTrue();
                    assertThat(result.totalCount()).isEqualTo(21);
                });
    }

    @Test
    @WithMockUser
    void delete() {
        User user = UserFixture.createUser();
        userRepository.save(user);
        Notification notification = Notification.create(user, "title", "content", NotificationLevel.INFO);
        Notification save = notificationRepository.save(notification);

        assertThat(
                mvcTester.delete().uri("/api/notifications/{notificationId}", save.getId())
                        .with(csrf())
        ).hasStatus(HttpStatus.NO_CONTENT);

        Optional<Notification> result = notificationRepository.findById(save.getId());
        assertThat(result).isEmpty();

    }
}