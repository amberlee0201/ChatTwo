package com.ce.chat2.participation.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class ParticipationNotFound extends CustomBaseException {

    public ParticipationNotFound() {
        super(ErrorCode.PARTICIPATION_NOT_FOUND);
    }
}
