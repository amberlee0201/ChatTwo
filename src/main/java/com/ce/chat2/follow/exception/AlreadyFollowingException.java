package com.ce.chat2.follow.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class AlreadyFollowingException extends CustomBaseException {
    public AlreadyFollowingException(String message) {
        super(ErrorCode.ALREADY_FOLLOWING);
    }
}
