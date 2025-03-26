package com.ce.chat2.chat.dto.response;

import com.ce.chat2.chat.entity.Chat;
import com.ce.chat2.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatResponseDto {
    private String roomId;
    private int userId;
    private String senderName;
    private String senderImage;
    private String content;
    private int readCnt;
    private String filePath;
    private String fileType;
    private String sendAt;
    private long timestamp;

    public static ChatResponseDto of(Chat chat, User sender, int readCnt, String roomId){
        return ChatResponseDto.builder()
            .roomId(roomId)
            .userId(sender.getId())
            .senderName(sender.getName())
            .senderImage(sender.getImage())
            .content(chat.getContent())
            .readCnt(readCnt)
            .filePath(chat.getFilePath())
            .fileType(chat.getFileType())
            .sendAt(chat.timeToString())
            .timestamp(chat.getCreatedAt().toEpochMilli())
            .build();
    }
}
