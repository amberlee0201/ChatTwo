package com.ce.chat2.participation.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;
    private final SimpMessageSendingOperations messagingTemplate;

    // 존 채팅방 목록 요청 (처음 화면 로딩 시 최초 1회)
    @MessageMapping("/rooms/init")
    public void sendMessage(Authentication authentication) {

        if (authentication.getPrincipal() instanceof Oauth2UserDetails userDetails) {
            userDetails = (Oauth2UserDetails) authentication.getPrincipal();
            String userId = String.valueOf(userDetails.getUser().getId());
            messagingTemplate.convertAndSend("/topic/user/" + userId,
                    participationService.sendInitialRooms(userId));
        } else {
            // @TODO
            throw new ClassCastException("authentication.getPrincipal() is not instanceof " + Oauth2UserDetails.class.getSimpleName());
        }
    }
}
