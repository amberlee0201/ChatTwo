package com.ce.chat2.notification.service;

import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

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
}