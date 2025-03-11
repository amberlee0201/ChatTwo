package com.ce.chat2.notification.repository;

import com.ce.chat2.notification.entity.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 참여자(participationId)의 알림 전체 조회
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Integer receiverId);

    // ✅ 참여자 ID로 알림 전체 삭제
    void deleteByReceiverId(Integer receiverId);

}