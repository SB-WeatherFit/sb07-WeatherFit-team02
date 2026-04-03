package com.codeit.weatherfit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //Follow
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자신을 팔로우할 수 없습니다.", "F-001"),
    INVALID_FOLLOW_ARGUMENT(HttpStatus.BAD_REQUEST, "팔로워와 팔로이는 반드시 존재해야 합니다.", "F-002"),
    ALREADY_FOLLOW_EXCEPTION(HttpStatus.BAD_REQUEST, "팔로우는 한 번만 할 수 있습니다", "F-003"),
    NOT_EXIST_FOLLOW(HttpStatus.BAD_REQUEST, "팔로우하지 않았습니다.", "F-004"),
    //Message
    NOT_SEND_SELF_MESSAGE(HttpStatus.BAD_REQUEST, "자신에게 메시지를 보낼 수 없습니다", "M-001"),
    INVALID_MESSAGE_ARGUMENT(HttpStatus.BAD_REQUEST, "발신자와 수신자는 반드시 존재해야 합니다.", "M-002"),
    MESSAGE_CONTENT_NULL(HttpStatus.BAD_REQUEST, "메세지의 내용을 정해주세요", "M-003"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "U-001"),
    DUPLICATED_USER_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.", "U-002"),
    INVALID_USER_EMAIL(HttpStatus.BAD_REQUEST, "이메일은 필수입니다.", "U-003"),
    INVALID_USER_NAME(HttpStatus.BAD_REQUEST, "이름은 필수입니다.", "U-004"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "사용자 역할이 올바르지 않습니다.", "U-005"),
    INVALID_USER_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 필수입니다.", "U-006"),

    // Profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필을 찾을 수 없습니다.", "P-001"),
    INVALID_PROFILE_USER(HttpStatus.BAD_REQUEST, "프로필 사용자 정보가 올바르지 않습니다.", "P-002"),
    INVALID_PROFILE_GENDER(HttpStatus.BAD_REQUEST, "성별은 필수입니다.", "P-003"),
    INVALID_PROFILE_TEMPERATURE_SENSITIVITY(HttpStatus.BAD_REQUEST, "온도 민감도는 1 이상 5 이하여야 합니다.", "P-004"),

    INVALID_SOCIAL_ACCOUNT_USER(HttpStatus.BAD_REQUEST, "소셜 계정 사용자 정보가 올바르지 않습니다.", "S-001"),
    INVALID_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "소셜 제공자 정보가 올바르지 않습니다.", "S-002"),
    INVALID_SOCIAL_PROVIDER_USER_ID(HttpStatus.BAD_REQUEST, "소셜 제공자 사용자 식별자가 올바르지 않습니다.", "S-003"),
    INVALID_SOCIAL_PROVIDER_EMAIL(HttpStatus.BAD_REQUEST, "소셜 제공자 이메일 정보가 올바르지 않습니다.", "S-004"),

    // Feed
    FEED_NOT_EXIST(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다.", "FE-001"),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 피드입니다.", "FE-002"),
    LIKE_NOT_EXIST(HttpStatus.BAD_REQUEST, "좋아요하지 않은 피드입니다.", "FE-003"),
    FEED_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "FE-004"),

    // Auth
    INVALID_SIGN_IN_REQUEST(HttpStatus.BAD_REQUEST, "이메일과 비밀번호는 필수입니다.", "A-001"),
    SIGN_IN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.", "A-002"),
    SIGN_OUT_FAILED(HttpStatus.UNAUTHORIZED, "로그아웃할 인증 정보가 올바르지 않습니다.", "A-003"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 올바르지 않습니다.", "A-004"),
    INVALID_RESET_PASSWORD_REQUEST(HttpStatus.BAD_REQUEST, "이메일은 필수입니다.", "A-005"),
    RESET_PASSWORD_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "비밀번호를 초기화할 사용자를 찾을 수 없습니다.", "A-006"),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 제공자입니다.", "A-007"),
    SOCIAL_SIGN_IN_FAILED(HttpStatus.UNAUTHORIZED, "소셜 로그인에 실패했습니다.", "A-008"),
    SOCIAL_ACCOUNT_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "동일한 이메일의 기존 계정이 있어 소셜 로그인을 진행할 수 없습니다.", "A-009"),
    // Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "CM-001"),

    //Weather
    WEATHER_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"날씨 정보를 불러오는데 실패했습니다","W-001"),
    WEATHER_NOT_FOUND(HttpStatus.NOT_FOUND,"날씨 정보를 DB에서 찾을 수 없습니다","W-002"),

    // file
    FILE_UPLOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.", "FI-001"),
    FILE_DELETE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.", "FI-002"),
    FILE_URL_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 URL 생성에 실패했습니다.", "FI-003"),

    // Clothes
    CLOTHES_NOT_FOUND(HttpStatus.NOT_FOUND, "옷을 찾을 수 없습니다.", "C-001"),
    CLOTHES_ATTRIBUTE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND,"속성 정의를 찾을 수 없습니다.", "C-002"),
    CLOTHES_ATTRIBUTE_VALUE_MISSING(HttpStatus.NOT_FOUND, "속성 값을 찾을 수 없습니다.", "C-003"),
    INVALID_CLOTHES_ATTRIBUTE_OPTION(HttpStatus.BAD_REQUEST, "잘못된 옵션입니다.", "C-004"),
    URL_PARSING_FAILED(HttpStatus.BAD_REQUEST,"잘못된 URL입니다." , "C-005");


    final HttpStatus httpStatus;
    final String message;
    final String code;

    ErrorCode(HttpStatus httpStatus, String message, String code) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.code = code;
    }
}