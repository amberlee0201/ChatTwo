package com.ce.chat2.notification.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.notification.dto.NotificationResponse;
import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(NotificationApiController.class);

    // ✅ 알림 페이지 진입
    @GetMapping("/notifications")
    public String notificationsPage(@AuthenticationPrincipal Oauth2UserDetails userDetails, Model model) {
        String userId = String.valueOf(userDetails.getUser().getId());
        List<NotificationResponse> notifications = notificationService.getNotifications(userId)
            .stream()
            .map(NotificationResponse::from)
            .toList();
        model.addAttribute("notifications", notifications); // ✅ 여기가 핵심
        model.addAttribute("user", userDetails.getUser());
        return "notification";
    }

    // ✅ 세션기반 알림 조회
    @ResponseBody
    @GetMapping("/notifications/data")
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
        String userId = String.valueOf(userDetails.getUser().getId());
        List<Notification> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // ✅ 전체 Soft Delete
    @ResponseBody
    @DeleteMapping("/notifications")
    public ResponseEntity<Void> deleteAllNotifications(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
        String userId = String.valueOf(userDetails.getUser().getId());
        log.info("🧽 알림 전체 soft-delete 시작 - userId: {}", userId); // 이 로그 꼭 확인!
        notificationService.hideAllByUserId(userId);
        return ResponseEntity.ok().build();
    }

}
