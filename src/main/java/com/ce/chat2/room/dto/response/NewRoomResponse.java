package com.ce.chat2.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewRoomResponse {
    private String roomId;
    private List<Integer> userIds;

    public static NewRoomResponse of(String roomId, List<Integer> userIds) {
        return NewRoomResponse.builder().roomId(roomId).userIds(userIds).build();
    }
}
