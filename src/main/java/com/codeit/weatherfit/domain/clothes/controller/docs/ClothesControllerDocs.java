package com.codeit.weatherfit.domain.clothes.controller.docs;

import com.codeit.weatherfit.domain.auth.security.WeatherFitUserDetails;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDtoCursorResponse;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "의상 관리", description = "옷 등록, 수정, 삭제, 조회 및 URL 추출 API")
public interface ClothesControllerDocs {

    @Operation(
            summary = "옷 등록",
            description = "옷 정보와 이미지를 multipart/form-data 형식으로 받아 새 옷을 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "옷 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClothesDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "415", description = "지원하지 않는 Content-Type")
    })
    ResponseEntity<ClothesDto> create(
            ClothesCreateRequest request,
            @Parameter(description = "업로드할 옷 이미지 파일") MultipartFile image
    );

    @Operation(
            summary = "옷 수정",
            description = "기존 옷 정보를 수정합니다. 요청은 multipart/form-data 형식이며 이미지 파일도 함께 수정할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "옷 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClothesDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "404", description = "해당 옷을 찾을 수 없음"),
            @ApiResponse(responseCode = "415", description = "지원하지 않는 Content-Type")
    })
    ResponseEntity<ClothesDto> update(
            @Parameter(
                    name = "clothesId",
                    description = "수정할 옷 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            UUID clothesId,
            ClothesUpdateRequest request,
            @Parameter(description = "변경할 옷 이미지 파일") MultipartFile image
    );

    @Operation(
            summary = "옷 삭제",
            description = "지정한 옷 정보를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "옷 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 옷을 찾을 수 없음")
    })
    ResponseEntity<Void> delete(
            @Parameter(
                    name = "clothesId",
                    description = "삭제할 옷 ID",
                    required = true,
                    in = ParameterIn.PATH,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            UUID clothesId
    );

    @Operation(
            summary = "옷 목록 조회",
            description = "소유자 기준으로 옷 목록을 조회합니다. 커서 기반 페이지네이션과 타입 필터를 지원합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "옷 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClothesDtoCursorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 조회 조건")
    })
    ClothesDtoCursorResponse getClothes(
            @Parameter(
                    description = "조회할 소유자 ID",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            UUID ownerId,

            @Parameter(
                    description = "다음 페이지 조회를 위한 커서 값"
            )
            String cursor,

            @Parameter(
                    description = "동일 커서에서 추가 정렬 기준으로 사용하는 ID"
            )
            UUID idAfter,

            @Parameter(
                    description = "옷 타입 필터",
                    schema = @Schema(implementation = ClothesType.class),
                    example = "TOP"
            )
            ClothesType type,

            @Parameter(
                    description = "조회 크기",
                    example = "20"
            )
            int size
    );

    @Operation(
            summary = "URL 기반 옷 정보 추출",
            description = "외부 상품 URL을 기반으로 옷 정보를 추출합니다. 로그인한 사용자의 ownerId를 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "옷 정보 추출 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClothesDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 URL"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    ClothesDto extractionFromUrl(
            @Parameter(
                    description = "옷 정보를 추출할 외부 URL",
                    required = true,
                    example = "https://example.com/product/123"
            )
            String url,

            @Parameter(hidden = true)
            WeatherFitUserDetails userDetails
    );
}