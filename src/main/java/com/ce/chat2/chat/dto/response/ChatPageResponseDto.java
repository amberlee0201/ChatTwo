package com.ce.chat2.chat.dto.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatPageResponseDto {
    private List<ChatResponseDto> chatResponseDtoList;
    private Map<String, String> lastEvaluatedKey ;

    public static ChatPageResponseDto of (List<ChatResponseDto> chatResponseDtoList, Map<String, String> lastEvaluatedKey){
        return ChatPageResponseDto.builder()
            .chatResponseDtoList(chatResponseDtoList)
            .lastEvaluatedKey(lastEvaluatedKey)
            .build();
    }
}
