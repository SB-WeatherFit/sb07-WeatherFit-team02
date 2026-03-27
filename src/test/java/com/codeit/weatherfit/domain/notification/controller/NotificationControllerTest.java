package com.codeit.weatherfit.domain.notification.controller;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.entity.Notification;
import com.codeit.weatherfit.domain.notification.entity.NotificationLevel;
import com.codeit.weatherfit.domain.notification.repository.NotificationRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; //수정
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication; //수정
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

//    @Test
//    void search() {
//        User user = UserFixture.createUser();
//        userRepository.save(user);
//
//        for (int i = 0; i < 40; i++) {
//            Notification notification = Notification.create(user, "title" + i, "content", NotificationLevel.INFO);
//            notificationRepository.save(notification);
//        }
//
//        em.flush();
//        em.clear();
//        NotificationSearchCondition condition = new NotificationSearchCondition(null, null, 20);
//        Map<String, String> params = objectMapper.convertValue(condition, new TypeReference<>() {
//        });
//        var requestBuilder = mvcTester.get().uri("/api/notifications");
//        params.forEach(requestBuilder::param);
//
//
//        assertThat(requestBuilder.exchange())
//                .apply(print())
//                .hasStatusOk();
//    }

    @Test
    void delete() {
        User user = UserFixture.createUser();
        userRepository.save(user);
        Notification notification = Notification.create(user, "title", "content", NotificationLevel.INFO);
        Notification save = notificationRepository.save(notification);
        em.flush();
        em.clear();

        WeatherFitUserDetails userDetails = WeatherFitUserDetails.from(user); //수정
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( //수정
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        assertThat(
                mvcTester.delete().uri("/api/notifications/{notificationId}", save.getId())
                        .with(csrf())
                        .with(authentication(authToken)) //수정
        ).hasStatus(HttpStatus.NO_CONTENT);
    }
}