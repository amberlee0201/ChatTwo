package com.ce.chat2.room.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomCreateRequest {
    private List<Integer> invitedIds;
}
