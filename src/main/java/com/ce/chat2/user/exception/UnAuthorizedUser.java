package com.ce.chat2.user.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class UnAuthorizedUser extends CustomBaseException {

    public UnAuthorizedUser() {
        super(ErrorCode.UNAUTHORIZED_USER);
    }
}
