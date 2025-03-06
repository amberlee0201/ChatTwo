package com.ce.chat2.chat.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class ChatSendException extends CustomBaseException {

    public ChatSendException(String message) {
        super(message, ErrorCode.CHAT_SEND_EXCEPTION);
    }
}
