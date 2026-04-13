package com.codeit.weatherfit.domain.message.controller.docs;

import com.codeit.weatherfit.domain.message.dto.request.MessageGetRequest;
import com.codeit.weatherfit.domain.message.dto.response.MessageCursorResponse;
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

@Tag(name = "다이렉트 메시지", description = "다이렉트 메시지 조회 API")
public interface MessageControllerDocs {

    @Operation(summary = "메시지 목록 조회", description = "특정 사용자와의 다이렉트 메시지를 커서 기반 페이지네이션으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MessageCursorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<MessageCursorResponse> getMessage(
            @ParameterObject MessageGetRequest request,
            @Parameter(hidden = true) UUID myId
    );
}
