package com.ce.chat2.common.exception;

public class MaxFileSizeExceededException extends CustomBaseException {

    public MaxFileSizeExceededException() {
        super(ErrorCode.MAX_FILE_SIZE_EXCEEDED_EXCEPTION);
    }
}
