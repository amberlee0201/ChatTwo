package com.ce.chat2.chat.dto;

import com.ce.chat2.chat.entity.Chat;
import com.ce.chat2.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketChatDto {
    private String roomId;
    private Chat chat;
    private User sender;
    private int participationSize;

    public static WebSocketChatDto of(String roomId, Chat chat, User sender, int participationSize){
        return WebSocketChatDto.builder()
            .roomId(roomId)
            .chat(chat)
            .sender(sender)
            .participationSize(participationSize)
            .build();
    }
}
