package com.ce.chat2.notification.service;

import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.repository.NotificationRepository;
import com.ce.chat2.notification.controller.NotificationController.NotificationMessage;
import com.ce.chat2.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ 알림 저장: receiverId 기준
    public void saveNotification(Integer receiverId, String subject, String message) {
        Notification notification = Notification.builder()
            .receiverId(receiverId)
            .subject(subject)
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }

    // ✅ 알림 조회: receiverId 기준
    public List<Notification> getNotifications(Integer receiverId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    // ✅ 알림 삭제: receiverId 기준
    public void deleteAllByUserId(Integer receiverId) {
        log.info("🧹 삭제 요청 - receiverId: {}", receiverId);
        notificationRepository.deleteByReceiverId(receiverId);
    }

    // ✅ 친구 추가 알림 처리
    public void sendFriendFollowNotification(User from, User to) {
        String message = from.getName() + "님이 친구 요청을 보냈습니다.";

        // 알림 저장
        saveNotification(to.getId(), "친구 추가", message);

        // WebSocket 실시간 알림 전송
        messagingTemplate.convertAndSend("/topic/notification-sub", new NotificationMessage(message));
        log.info("📨 친구 알림 전송 완료: {}", message);
    }
}