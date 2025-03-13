package com.ce.chat2.notification.event;

import com.ce.chat2.notification.controller.NotificationApiController.NotificationMessage;
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

        // 메시지 생성
        String message = event.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = event.getFrom().getName() + "님이 친구 요청을 보냈습니다.";
        }

        // 알림 저장
        notificationService.saveNotification(event.getTo().getId(), "친구 추가", message);

        // WebSocket 메시지 전송
        messagingTemplate.convertAndSend("/topic/notification-sub", new NotificationMessage(message));
        messagingTemplate.convertAndSend("/topic/notification-count/" + event.getTo().getId(), 1);
        log.info("📨 친구 알림 전송 완료: {}", message);
    }
}