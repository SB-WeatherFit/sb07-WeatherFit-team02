package com.codeit.weatherfit.domain.notification.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Tag(name = "SSE", description = "Server-Sent Events 실시간 알림 구독 API")
public interface SseControllerDocs {

    @Operation(
            summary = "SSE 구독",
            description = "실시간 알림을 수신하기 위한 SSE 연결을 생성합니다. text/event-stream 형식으로 이벤트가 전송됩니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 성공",
                    content = @Content(mediaType = "text/event-stream")
            )
    })
    ResponseEntity<SseEmitter> subscribe(
            @Parameter(hidden = true) UUID userId,
            @Parameter(description = "마지막으로 수신한 이벤트 ID (재연결 시 사용)") UUID lastEventId
    );
}
