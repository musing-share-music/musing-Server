package com.example.musing.auth.exception;

import com.example.musing.exception.CustomException;
import com.example.musing.exception.ErrorCode;

public class AuthorityException extends CustomException {
    //권한 관련 에러코드 반환
    public AuthorityException(ErrorCode errorCode){
        super(errorCode);
    }
}
