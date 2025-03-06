package com.ce.chat2.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //Global
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "GLOBAL_INVALID_INPUT_VALUE", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL_METHOD_NOT_ALLOWED","잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_INTERNAL_SERVER_ERROR","내부 서버 에러가 발생했습니다."),
    NOT_SUPPORTED_METHOD_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL_NOT_SUPPORTED_METHOD_ERROR","지원하지 않는 Method 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL_404_ERROR", "404 에러입니다. 요청한 데이터를 서버가 찾을 수 없습니다."),
    REQUEST_IS_NOT_VALID(HttpStatus.BAD_REQUEST, "REQUEST_IS_NOT_VALID","request 값이 잘못되었습니다. 혹은 json 역직렬화를 할 수 없는 형식입니다."),

    // Follow
    FOLLOW_NOT_FOUND(HttpStatus.BAD_REQUEST, "FOLLOW_NOT_FOUND", "이 사용자는 친구가 아닙니다."),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "ALREADY_FOLLOWING", "이미 친구입니다."),

    //User
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST,"USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "UNAUTHORIZED_USER", "권한이 없는 유저입니다."),

    //Room
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROOM_NOT_FOUND","존재하지 않는 채팅방입니다."),

    //Chat
    CHAT_SEND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT_SEND_EXCEPTION", "메세지 전송중 오류가 발생했습니다."),

    //Participation
    PARTICIPATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "PARTICIPATION_NOT_FOUND", "참여 정보를 찾을 수 없습니다."),

    //AWS
    UNAVAILABLE_S3(HttpStatus.SERVICE_UNAVAILABLE, "UNAVAILABLE_S3", "S3 서비스에러가 발생했습니다."),;

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
