package com.ce.chat2.notification.controller;

import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userId) {
        List<Notification> result = notificationService.getNotifications(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> saveNotification(
        @RequestParam String userId,
        @RequestParam String subject,
        @RequestParam String message
    ) {
        notificationService.saveNotification(userId, subject, message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> hideAll(@PathVariable String userId) {
        notificationService.hideAllByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
