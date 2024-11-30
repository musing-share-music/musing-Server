package com.example.musing.auth.exception;

import com.example.musing.exception.CustomException;


public class TokenException extends CustomException {
    //토큰 관련 에러코드 반환
    public TokenException(ErrorCode errorCode){
        super(errorCode);
    }

}
