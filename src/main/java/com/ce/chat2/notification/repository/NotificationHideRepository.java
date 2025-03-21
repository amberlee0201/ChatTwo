package com.ce.chat2.notification.repository;

import com.ce.chat2.notification.entity.NotificationHide;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface NotificationHideRepository extends JpaRepository<NotificationHide, Long> {

    @Query("SELECT h.notificationId FROM NotificationHide h WHERE h.userId = :userId")
    Set<Integer> findNotificationIdsByUserId(Integer userId);
}
