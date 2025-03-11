package com.ce.chat2.notification.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/notifications") // ✅ API 경로는 분리
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(NotificationApiController.class);


    @GetMapping
    public List<Notification> getNotifications(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
        // 실제 알림 목록 반환
        return notificationService.getNotifications(userDetails.getUser().getId());
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllNotifications(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
        Integer userId = userDetails.getUser().getId();
        log.info("🧽 알림 삭제 시작 - receiverId: {}", userId); // ✅ 이거 추가!
        notificationService.deleteAllByUserId(userId);
        return ResponseEntity.ok().build();
    }
}