package com.codeit.weatherfit.domain.message.service;

import com.codeit.weatherfit.domain.message.dto.request.MessageCreateRequest;
import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
import com.codeit.weatherfit.domain.message.dto.response.SortBy;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.message.repository.MessageRepository;
import com.codeit.weatherfit.domain.message.service.event.MessageCreatedEvent;
import com.codeit.weatherfit.domain.profile.entity.Profile;
import com.codeit.weatherfit.domain.profile.repository.ProfileRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.codeit.weatherfit.domain.message.entity.UserFixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
@Transactional
class MessageServiceTest {

    @Autowired
    MessageService messageService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    EntityManager em;

    @Test
    void send() {
        User sender = userRepository.save(UserFixture.createUser());
        User receiver = userRepository.save(UserFixture.createUser("test2@gmail.com"));
        Profile profile = ProfileFixture.createProfile(sender);
        Profile profile2 = ProfileFixture.createProfile(receiver);
        profileRepository.save(profile);
        profileRepository.save(profile2);
        MessageCreateRequest request = new MessageCreateRequest(
                sender.getId(),
                receiver.getId(),
                "hello"
        );

        messageService.send(request);


        List<MessageCreatedEvent> events = applicationEvents.stream(MessageCreatedEvent.class)
                .toList();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().dmDto().content()).isEqualTo("hello");
    }

    @Test
    void searchMessages() {
        User user = createUser();
        User user2 = createUser("test2@gmail.com");
        Profile profile = ProfileFixture.createProfile(user);
        Profile profile2 = ProfileFixture.createProfile(user2);
        userRepository.save(user);
        userRepository.save(user2);
        profileRepository.save(profile);
        profileRepository.save(profile2);

        for (int i = 0; i < 50; i++) {
            Message message;
            if (i%2==0) message= Message.create(user, user2, "content"+i);
            else  message = Message.create(user2, user, "content"+i);
            messageRepository.save(message);
        }

        em.flush();
        em.clear();

        MessageGetRequest request = new MessageGetRequest(user2.getId(), null, null, 20);
        MessageCursorResponse result = messageService.searchMessages(request, user.getId());

        assertThat(result.data().size()).isEqualTo(request.limit());
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();
        assertThat(result.nextIdAfter()).isNotNull();
        assertThat(result.totalCount()).isEqualTo(50);
        assertThat(result.sortBy()).isEqualTo(SortBy.CREATED_AT);
    }
}