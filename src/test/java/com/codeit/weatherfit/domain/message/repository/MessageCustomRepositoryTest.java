package com.codeit.weatherfit.domain.message.repository;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.message.entity.UserFixture;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.config.JpaAuditingConfig;
import com.codeit.weatherfit.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.codeit.weatherfit.domain.message.entity.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
class MessageCustomRepositoryTest {

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    void searchMessages() {
        User user = createUser();
        User user2 = createUser("test2@gmail.com");
        userRepository.save(user);
        userRepository.save(user2);

        for (int i = 0; i < 50; i++) {
            Message message;
            if (i%2==0) message= Message.create(user, user2, "content"+i);
            else  message = Message.create(user2, user, "content"+i);
            messageRepository.save(message);
        }

        em.flush();
        em.clear();

        MessageGetRequest request = new MessageGetRequest(user.getId(), null, null, 20);
        List<Message> messages = messageRepository.searchMessages(request, user2.getId());

        assertThat(messages.size()).isEqualTo(21);
        assertThat(messages).allSatisfy(m ->
                assertThat(m.getReceiver().getId()).isEqualTo(user.getId())
        );
        assertThat(messages).allSatisfy(m ->
                assertThat(m.getSender().getId()).isEqualTo(user2.getId())
        );
        assertThat(messages.getFirst().getCreatedAt()).isAfter(messages.getLast().getCreatedAt());
    }
}