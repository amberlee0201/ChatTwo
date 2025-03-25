package com.ce.chat2.chat.controller;

import com.ce.chat2.chat.dto.request.ChatHistoryRequestDto;
import com.ce.chat2.chat.dto.response.ChatPageResponseDto;
import com.ce.chat2.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatApiController {
    private final ChatService chatService;
    @PostMapping
    ResponseEntity<ChatPageResponseDto> chatHistory(@RequestBody ChatHistoryRequestDto chatHistoryRequestDto){
        return new ResponseEntity<>(chatService.getChatPage(chatHistoryRequestDto), HttpStatus.OK);
    }
}
