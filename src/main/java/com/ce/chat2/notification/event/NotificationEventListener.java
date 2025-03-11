package com.ce.chat2.notification.event;

import com.ce.chat2.notification.service.NotificationService;
import com.ce.chat2.notification.event.FriendFollowedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleFriendFollowed(FriendFollowedEvent event) {
        log.info("📨 친구 추가 이벤트 수신 - from: {}, to: {}",
            event.getFrom().getName(), event.getTo().getName());

        notificationService.sendFriendFollowNotification(event.getFrom(), event.getTo());
    }
}