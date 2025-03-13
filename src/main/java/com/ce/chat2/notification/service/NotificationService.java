package com.ce.chat2.notification.service;

import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.entity.NotificationHide;
import com.ce.chat2.notification.repository.NotificationHideRepository;
import com.ce.chat2.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationHideRepository notificationHideRepository;

    public void saveNotification(Integer receiverId, String subject, String message) {
        Notification notification = Notification.builder()
            .receiverId(receiverId)
            .subject(subject)
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Integer receiverId) {
        Set<Integer> hiddenIds = notificationHideRepository.findNotificationIdsByUserId(receiverId);

        if (hiddenIds == null || hiddenIds.isEmpty()) {
            return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        }

        return notificationRepository.findByReceiverIdAndIdNotInOrderByCreatedAtDesc(receiverId, hiddenIds);
    }

    public void hideAllByUserId(Integer receiverId) {
        log.info("🙈 알림 숨김 처리 시작 - receiverId: {}", receiverId);

        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);

        List<NotificationHide> hides = notifications.stream()
            .map(notification -> NotificationHide.builder()
                .userId(receiverId)
                .notificationId(notification.getId())
                .hiddenAt(LocalDateTime.now())
                .build())
            .toList();

        notificationHideRepository.saveAll(hides);
    }
}
