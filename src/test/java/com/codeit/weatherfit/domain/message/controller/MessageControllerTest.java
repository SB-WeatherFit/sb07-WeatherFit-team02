package com.codeit.weatherfit.domain.message.controller;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.message.repository.MessageRepository;
import com.codeit.weatherfit.domain.message.service.MessageService;
import com.codeit.weatherfit.domain.message.service.ProfileFixture;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static com.codeit.weatherfit.domain.message.entity.UserFixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    MessageService messageService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MockMvcTester mvcTester;

    @Test
    void getMessageTest() {
        User user = createUser();
        User user2 = createUser("test2@gmail.com");
        Profile profile = ProfileFixture.createProfile(user);
        Profile profile2 = ProfileFixture.createProfile(user2);
        userRepository.save(user);
        userRepository.save(user2);
        profileRepository.save(profile);
        profileRepository.save(profile2);
        for (int i = 0; i < 21; i++) {
            Message message;
            if (i % 2 == 0) message = Message.create(user, user2, "content" + i);
            else message = Message.create(user2, user, "content" + i);
            messageRepository.save(message);
        }
        WeatherFitUserDetails details = WeatherFitUserDetails.from(user2);

        assertThat(
                mvcTester.get()
                        .uri("/api/direct-messages")
                        .with(csrf())
                        .with(user(details))
                        .param("userId", user.getId().toString())
                        .param("limit", "20"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .convertTo(MessageCursorResponse.class)
                .satisfies(result -> {
                    assertThat(result.data().size()).isEqualTo(20);
                    assertThat(result.totalCount()).isEqualTo(21);
                    assertThat(result.hasNext()).isTrue();
                });
    }
}