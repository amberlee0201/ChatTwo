package com.ce.chat2.room.dto;

import com.ce.chat2.room.entity.Room;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RoomResponse {
    private String roomId;
    private String roomName;
    private String latestMessage;
    private Instant latestTimestamp;

    public static RoomResponse of(Room room) {
        return RoomResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .latestMessage(room.getLatestMessage())
                .latestTimestamp(room.getLatestTimestamp())
                .build();
    }
}
