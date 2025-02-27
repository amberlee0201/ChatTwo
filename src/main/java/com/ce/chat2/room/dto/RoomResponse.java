package com.ce.chat2.room.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class RoomResponse {
    private String roomId;
    private String roomName;
    private String latestMessage;
    private Instant latestTimestamp;
}
