package com.ce.chat2.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadCountResponseDto {
    private int userId;
    private long firstReadChatTime;
    private long lastReadChatTime;

    public static ReadCountResponseDto of(int userId, long firstReadChatTime, long lastReadChatTime){
        return ReadCountResponseDto.builder()
            .userId(userId)
            .firstReadChatTime(firstReadChatTime)
            .lastReadChatTime(lastReadChatTime)
            .build();
    }
}
