package com.ce.chat2.room.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomInviteResponse {
    private String roomId;

    public static RoomInviteResponse of(String roomId) {
        return RoomInviteResponse.builder().roomId(roomId).build();
    }
}
