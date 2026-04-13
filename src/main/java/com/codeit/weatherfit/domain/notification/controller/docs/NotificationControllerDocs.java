package com.codeit.weatherfit.domain.notification.controller.docs;

import com.codeit.weatherfit.domain.notification.dto.request.NotificationSearchCondition;
import com.codeit.weatherfit.domain.notification.dto.response.NotificationCursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "알림", description = "알림 조회 및 삭제 API")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 목록 조회", description = "커서 기반 페이지네이션으로 알림 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = NotificationCursorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<NotificationCursorResponse> findNotifications(
            @ParameterObject NotificationSearchCondition condition,
            @Parameter(hidden = true) UUID myId
    );

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    ResponseEntity<Void> deleteNotification(
            @Parameter(description = "알림 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID notificationId
    );

    @Operation(summary = "알림 전체 삭제", description = "특정 사용자의 모든 알림을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "전체 삭제 성공")
    })
    ResponseEntity<Void> deleteAllNotification(
            @Parameter(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId
    );
}
