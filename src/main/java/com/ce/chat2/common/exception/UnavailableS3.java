package com.ce.chat2.common.exception;

public class UnavailableS3 extends CustomBaseException {

    public UnavailableS3() {
        super(ErrorCode.UNAVAILABLE_S3);
    }

    public UnavailableS3(String message){
        super(message, ErrorCode.UNAVAILABLE_S3);
    }
}
