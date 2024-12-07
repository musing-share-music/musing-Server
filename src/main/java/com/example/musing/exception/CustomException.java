package com.example.musing.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    public final ErrorCode errorCode;
    public final String message;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
    public CustomException(ErrorCode errorCode,String message){
        super(message);
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
