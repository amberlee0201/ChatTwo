package com.ce.chat2.chat.dto.request;

import lombok.Data;

@Data
public class ReadCountRequestDto {
    private int userId;
    private long chatTime;
    private String roomId;
}
