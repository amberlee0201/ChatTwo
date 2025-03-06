package com.ce.chat2.room.dto.response;

import com.ce.chat2.room.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
