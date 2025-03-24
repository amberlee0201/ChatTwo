package com.ce.chat2.common.exception;

public class UnsupportedFileFormatException extends CustomBaseException {

    public UnsupportedFileFormatException() {
        super(ErrorCode.UNSUPPORTED_FILE_FORMAT_EXCEPTION);
    }
}
