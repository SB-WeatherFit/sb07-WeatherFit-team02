package com.codeit.weatherfit.global.s3.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "S3", description = "S3 로그 업로드 API")
public interface S3ControllerDocs {

    @Operation(summary = "로그 파일 업로드", description = "로그 파일을 S3에 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "500", description = "업로드 실패")
    })
    ResponseEntity<Void> addS3();
}
