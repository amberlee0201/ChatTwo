package com.ce.chat2.chat.dto;

import com.ce.chat2.chat.dto.response.ChatResponseDto;
import com.ce.chat2.user.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private List<ChatResponseDto> chatResponseDtoList;
    private List<User> participants;

    public static ChatRoomDto of(List<ChatResponseDto> chatResponseDtoList, List<User> participants){
        return ChatRoomDto.builder()
            .chatResponseDtoList(chatResponseDtoList)
            .participants(participants)
            .build();
    }
}
