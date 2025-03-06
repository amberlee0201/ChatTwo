package com.ce.chat2.room.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomListResponse {
    private List<String> rooms;
}
