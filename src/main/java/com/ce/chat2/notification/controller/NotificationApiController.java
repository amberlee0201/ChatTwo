package com.ce.chat2.notification.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(NotificationApiController.class);

    @GetMapping
    public List<Notification> getNotifications(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
        return notificationService.getNotifications(userDetails.getUser().getId());
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllNotifications(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
        log.info("🧽 알림 삭제 시작 - receiverId: {}", userDetails.getUser().getId());
        notificationService.hideAllByUserId(userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-test-notification")
    public ResponseEntity<String> sendTestNotification(@RequestParam String message) {
        messagingTemplate.convertAndSend("/topic/notification-sub", new NotificationMessage(message));
        return ResponseEntity.ok("테스트 메시지 전송 완료: " + message);
    }

    @Data
    public static class NotificationMessage {
        private String message;

        public NotificationMessage(String message) {
            this.message = message;
        }
    }
}