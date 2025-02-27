package com.ce.chat2.room.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomCreateResponse {
    private String roomId;
}
