package com.ce.chat2.notification.service;

import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void saveNotification(String userId, String subject, String message) {
        Notification notification = Notification.of(userId, subject, message);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserId(userId).stream()
            .filter(n -> !Boolean.TRUE.equals(n.getIsDeleted()))
            .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public void hideAllByUserId(String userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId); // ✅ 변경

        List<Notification> toUpdate = notifications.stream()
            .filter(n -> !Boolean.TRUE.equals(n.getIsDeleted()))
            .peek(n -> {
                n.setIsDeleted(true);
                n.setDeletedAt(Instant.now());
            })
            .collect(Collectors.toList());

        notificationRepository.saveAll(toUpdate);
    }

    public int getActiveNotificationCount(String userId) {
        return (int) notificationRepository.findByUserId(userId).stream()
            .filter(n -> !Boolean.TRUE.equals(n.getIsDeleted()))
            .count();
    }
}
