package com.ce.chat2.follow.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class FollowNotFoundException extends CustomBaseException {
    public FollowNotFoundException() {
        super(ErrorCode.FOLLOW_NOT_FOUND);
    }

}
