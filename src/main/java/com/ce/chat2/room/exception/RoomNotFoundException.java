package com.ce.chat2.room.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

// 해당 roomId의 방이 존재하지 않을 경우
public class RoomNotFoundException extends CustomBaseException {
    public RoomNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND);
    }
}
