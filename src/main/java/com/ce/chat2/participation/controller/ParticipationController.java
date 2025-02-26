package com.ce.chat2.participation.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // 존 채팅방 목록 요청 (처음 화면 로딩 시 최초 1회)
    @MessageMapping("/rooms/init/{userId}")
    @SendTo("/topic/user/{userId}")
    public List<String> sendMessage(@DestinationVariable String userId,
                                    Authentication authentication) {

        if (authentication.getPrincipal() instanceof Oauth2UserDetails userDetails) {
            log.info("sendMessage: userId={}, userDetails={}", userId, userDetails.getUser().getId());
        }
        // @TODO 서버에서 최신순으로 정렬 후 초기전송?
        return participationService.sendInitialRooms(userId);
    }

}
