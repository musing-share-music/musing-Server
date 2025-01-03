package com.example.musing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //JWT관련 예외처리
    ILLEGAL_REGISTRATION_ID(NOT_ACCEPTABLE, "illegal registration id"),
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "올바르지 않은 토큰입니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "잘못된 JWT 시그니처입니다."),

    //권한 설정 예외처리
    INVALID_AUTHORITY(UNAUTHORIZED, "잘못된 권한 설정입니다"),

    //유저 정보를 읽을 수 없을 때
    NOT_FOUND_USER(NOT_FOUND, "유저 정보를 찾을 수 없습니다."),

    //리뷰 관련 예외처리
    NOT_FOUND_REPLY(NOT_FOUND, "리뷰를 확인할 수 없습니다."),
    NOT_MATCHED_REPLY_AND_USER(CONFLICT, "유저와 리뷰 작성자가 일치하지 않습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
