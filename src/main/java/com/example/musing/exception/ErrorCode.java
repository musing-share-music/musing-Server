package com.example.musing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 임시 사용 예외처리
    ERROR(BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),
    //플레이리스트 예외처리

    FAILED_TO_FETCH_PLAYLIST(NOT_FOUND,"플레이 리스트를 불러오지 못했습니다."),
    PRIVATE_PLAYLIST(NOT_FOUND,"플레이리스트가 비공개상태입니다."),
    MISSING_PLAYLIST_THUMBNAIL(NOT_FOUND,"섬네일 이미지를 찾을 수 없습니다."),
    MISSING_PLAYLIST_SNIPPET(NOT_FOUND,"재생목록 정보를 불러올 수 없습니다."),
    MISSING_PRIVACY_STATUS(NOT_FOUND,"공개여부 조회가 불가능합니다."),
    MISSING_PLAYLIST_STATUS(NOT_FOUND,"상태여부를 확인 할 수 없습니다."),
    // JWT관련 예외처리
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "올바르지 않은 토큰입니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "잘못된 JWT 시그니처입니다."),
    NEW_ENVIRONMENT_LOGIN(UNAUTHORIZED, "새로운 환경에 로그인했습니다."),

    // 잘못된 접근 예외처리
    INVALID_ACCESS(FORBIDDEN, "잘못된 접근입니다."),

    // 권한 설정 예외처리
    INVALID_AUTHORITY(UNAUTHORIZED, "잘못된 권한 설정입니다"),
    UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN(UNAUTHORIZED, "Oauth2 프로바이더의 토큰을 발급 받을 수 없습니다."),

    // 유저 정보를 읽을 수 없을 때
    NOT_FOUND_USER(NOT_FOUND, "유저 정보를 찾을 수 없습니다."),

    // 리뷰 관련 예외처리
    EXIST_REPLY(CONFLICT, "이미 작성한 리뷰가 있습니다."),
    NOT_FOUND_REPLY(NOT_FOUND, "리뷰를 확인할 수 없습니다."),
    NOT_MATCHED_REPLY_AND_USER(CONFLICT, "해당 유저와 작성자가 일치하지 않습니다."),
    BAD_REQUEST_REPLY_PAGE(BAD_REQUEST, "해당 페이지는 존재하지 않습니다."),


    // 게시판 관련 예외처리
    NOT_FOUND_BOARD(NOT_FOUND, "해당 게시글을 불러올 수 없습니다."),
    NOT_FOUND_KEYWORD(NOT_FOUND, "해당 키워드로 검색할 수 없습니다."),
    NOT_MATCHED_BOARD_AND_USER(CONFLICT, "해당 유저와 작성자가 일치하지 않습니다."),
    BAD_REQUEST_BOARD_PAGE(BAD_REQUEST, "해당 페이지는 존재하지 않습니다."),


    // 음악 관련 예외처리
    NOT_FOUND_MUSIC(NOT_FOUND, "해당 노래를 찾을 수 없습니다"),
    NOT_FOUND_GENRE(NOT_FOUND, "해당 장르를 찾을 수 없습니다"),
    NOT_FOUND_MOOD(NOT_FOUND, "해당 분위기를 찾을 수 없습니다"),
    NOT_FOUND_ARTIST(NOT_FOUND, "해당 아티스트를 찾을 수 없습니다"),
    
    //공지사항 관련 예외처리
    NOT_FOUND_NOTICE(NOT_FOUND, "해당 공지사항을 찾을 수 없습니다"),

    //신고 관련 예외처리
    BAD_REQUEST_REPORT_PAGE(BAD_REQUEST, "해당 페이지는 존재하지 않습니다."),

    // 알람 관련 예외처리
    NOT_FOUND_ALARM(NOT_FOUND, "알람이 없습니다."),
    NOT_FOUND_ALARM_TYPE(NOT_FOUND, "지정되지 않은 알람 종류 입니다."),

    //좋아하는 장르 관련 예외처리
    NOT_FOUND_LIKE_GENRE(NOT_FOUND, "최소 한개 이상의 좋아하는 장르는 선택해야합니다."),
    //좋아하는 분위기 관련 예외처리
    NOT_FOUND_LIKE_MOOD(NOT_FOUND, "최소 한개 이상의 좋아하는 장르는 선택해야합니다."),
    //좋아하는 가수 관련 예외처리
    NOT_FOUND_LIKE_ARTIST(NOT_FOUND, "최소 한개 이상의 좋아하는 장르는 선택해야합니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
