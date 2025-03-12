package com.ce.chat2.chat.controller;

import com.ce.chat2.chat.dto.ChatRoomDto;
import com.ce.chat2.chat.dto.request.ChatRequestDto;
import com.ce.chat2.chat.dto.request.ReadCountRequestDto;
import com.ce.chat2.chat.service.ChatService;
import com.ce.chat2.chat.service.ReadCountService;
import com.ce.chat2.chat.service.RedisChatPubSubService;
import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final RedisChatPubSubService redisChatPubSubService;
    private final ReadCountService readCountService;

    @GetMapping("/chats/{roomId}")
    String toChat(@AuthenticationPrincipal Oauth2UserDetails loginUser,
        @PathVariable("roomId") String roomId,
        Model model
    ){
        ChatRoomDto chatRoomDto = chatService.getChatHistory(roomId);

        model.addAttribute("user", loginUser.getUser());
        model.addAttribute("roomId", roomId);
        model.addAttribute("chatHistory", chatRoomDto.getChatResponseDtoList());
        model.addAttribute("participants", chatRoomDto.getParticipants());
        return "chat";
    }

    @MessageMapping("/{roomId}")
    public void sendMessage(
        @DestinationVariable("roomId") String roomId,
        ChatRequestDto chatRequestDto
    ) throws JsonProcessingException {
        chatRequestDto.setRoomId(roomId);

        ObjectMapper objectMapper = new ObjectMapper();
        String req = objectMapper.writeValueAsString(chatRequestDto);

        redisChatPubSubService.publish("chat"+roomId, req);
    }

    @MessageMapping("/count/{roomId}")
    public void readCntProcess(@DestinationVariable("roomId") String roomId,
        Authentication authentication,
        ReadCountRequestDto readCountRequestDto)
        throws JsonProcessingException {
        Oauth2UserDetails auth = (Oauth2UserDetails) authentication.getPrincipal();
        readCountRequestDto.setUserId(auth.getUser().getId());
        readCountRequestDto.setRoomId(roomId);
        ObjectMapper objectMapper = new ObjectMapper();
        String req = objectMapper.writeValueAsString(readCountRequestDto);

        readCountService.publish("readCount"+roomId, req);
    }
}
