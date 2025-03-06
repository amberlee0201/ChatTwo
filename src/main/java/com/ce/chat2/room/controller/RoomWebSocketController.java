package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoomWebSocketController {
    private final RoomService roomService;

    // 기존 채팅방 목록 요청 (처음 room-list 화면 로딩 시 최초 1회)
    @MessageMapping("/rooms/init")
    public void sendMessage(Authentication authentication) {

        if (authentication.getPrincipal() instanceof Oauth2UserDetails userDetails) {
            userDetails = (Oauth2UserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getUser().getId();
            roomService.sendInitialRooms(userId);
        } else {
            throw new ClassCastException("authentication.getPrincipal() is not instanceof " + Oauth2UserDetails.class.getSimpleName());
        }
    }
}
