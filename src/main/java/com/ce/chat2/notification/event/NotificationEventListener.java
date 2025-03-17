package com.ce.chat2.notification.event;

import com.ce.chat2.notification.service.NotificationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleFriendFollowed(FriendFollowedEvent event) {
        String receiverId = String.valueOf(event.getTo().getId());
        String message = event.getMessage();
        String subject = "친구 추가 알림";

        // ✅ 알림 저장
        notificationService.saveNotification(receiverId, subject, message);

        // ✅ 삭제되지 않은 알림 개수 조회
        int unreadCount = notificationService.getActiveNotificationCount(receiverId);

        // ✅ WebSocket 실시간 전송 (알림 카운트 + 알림 새로고침 트리거용)
        Map<String, Object> payload = Map.of(
            "message", message,
            "count", unreadCount
        );

        messagingTemplate.convertAndSend("/topic/notification-sub/" + receiverId, payload);

    }
}
