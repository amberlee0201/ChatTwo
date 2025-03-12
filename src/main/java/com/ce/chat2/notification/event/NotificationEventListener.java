package com.ce.chat2.notification.event;

import com.ce.chat2.notification.service.NotificationService;
import com.ce.chat2.notification.event.FriendFollowedEvent;
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
        log.info("📨 친구 추가 이벤트 수신 - from: {}, to: {}",
            event.getFrom().getName(), event.getTo().getName());

        notificationService.sendFriendFollowNotification(event.getFrom(), event.getTo());
    }

    @EventListener
    public void handleNotificationEvent(FriendFollowedEvent event) {
        // 알림 메시지 전송
        String message = event.getMessage();

        // WebSocket을 통해 알림 메시지를 모든 클라이언트에 전송
        messagingTemplate.convertAndSend("/topic/notification-sub", message);

        // 추가: 알림 카운트 업데이트
        messagingTemplate.convertAndSend("/topic/notification-count", 1); // 1씩 증가
    }
}