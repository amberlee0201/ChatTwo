package com.ce.chat2.chat.dto.request;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ChatHistoryRequestDto {
    private String roomId;
    private Map<String, String> lastKey = new HashMap<>();
}
