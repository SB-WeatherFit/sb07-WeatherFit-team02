package com.codeit.weatherfit.domain.message.dto.response;

import com.codeit.weatherfit.domain.message.entity.Message;
import com.codeit.weatherfit.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        @Schema(description = "메시지 ID") UUID messageId,
        @Schema(description = "생성일시") Instant createdAt,
        @Schema(description = "발신자 정보") MessageUser sender,
        @Schema(description = "수신자 정보") MessageUser receiver,
        @Schema(description = "메시지 내용") String content
) {
    public static MessageDto from(
            Message message,
            User sender,
            String senderProfileImageUrl,
            User receiver,
            String receiverProfileImageUrl) {
        MessageUser senderUser = MessageUser.from(sender, senderProfileImageUrl);
        MessageUser receiverUser = MessageUser.from(receiver, receiverProfileImageUrl);
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                senderUser,
                receiverUser,
                message.getContent()
        );
    }
}
