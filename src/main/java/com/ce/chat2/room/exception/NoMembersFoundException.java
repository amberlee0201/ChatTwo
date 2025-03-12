package com.ce.chat2.room.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

// 채팅방에 초대 가능한 친구 목록이 없을 때
public class NoMembersFoundException extends CustomBaseException {
    public NoMembersFoundException() {
        super(ErrorCode.NO_MEMBERS_FOUND);
    }
}
