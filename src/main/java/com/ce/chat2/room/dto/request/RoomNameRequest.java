package com.ce.chat2.room.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomNameRequest {
    private String roomName;
}
