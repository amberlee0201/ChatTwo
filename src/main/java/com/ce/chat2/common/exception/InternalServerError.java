package com.ce.chat2.common.exception;

public class InternalServerError extends CustomBaseException {

    public InternalServerError() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
