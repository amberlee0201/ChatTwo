package com.ce.chat2.chat.dto.request;

import lombok.Data;

@Data
public class ChatRequestDto {
    private String roomId;
    private String content;
    private String senderName;
    private int userId;
}
