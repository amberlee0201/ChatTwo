package com.ce.chat2.notification.dto;

import com.ce.chat2.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private String notificationId;
    private String subject;
    private String message;
    private Long createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
            .notificationId(notification.getNotificationId())
            .subject(notification.getSubject())
            .message(notification.getMessage())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
