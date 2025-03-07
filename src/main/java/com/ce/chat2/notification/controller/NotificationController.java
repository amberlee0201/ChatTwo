package com.ce.chat2.notification.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.user.entity.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Data
    public static class NotificationMessage {
        private String message;

        public NotificationMessage(String message) {
            this.message = message;
        }
    }

    @PostMapping("/friend-added")
    public void sendFriendNotification(@RequestParam String sender, @RequestParam String receiver) {
        String message = sender + "님이 친구 요청을 보냈습니다.";
        log.info("🔔 친구 추가 알림 전송: {}", message);

        // WebSocket을 통해 `/topic/notification-sub`으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/notification-sub", new NotificationMessage(message));
    }


    @GetMapping  // ✅ 여기서는 빈 값("")으로 설정 ("/notifications" 자동 적용)
    public String getNotifications(Model model, @AuthenticationPrincipal Oauth2UserDetails oAuth2User) {
        if (oAuth2User != null) {
            model.addAttribute("user", oAuth2User.getUser()); // ✅ User 객체를 모델에 추가
            log.info("🔵 알림 페이지 접속 - 로그인된 사용자 ID: {}", oAuth2User.getUser().getId());
        } else {
            log.warn("🟠 알림 페이지 접속 - OAuth2 인증 정보 없음!");
        }
        return "notification";
    }

}