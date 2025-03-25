package com.ce.chat2.notification.repository;

import com.ce.chat2.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<Notification> table() {
        return enhancedClient.table("Chat2Notification", TableSchema.fromBean(Notification.class));
    }

    public void save(Notification notification) {
        table().updateItem(notification);
    }

    public List<Notification> findByUserId(String userId) {
        QueryConditional condition = QueryConditional
            .keyEqualTo(Key.builder().partitionValue(userId).build());

        List<Notification> result = new ArrayList<>();
        table().query(r -> r.queryConditional(condition))
            .items()
            .forEach(n -> {
                if (!Boolean.TRUE.equals(n.getIsDeleted())) {
                    result.add(n);
                }
            });
        return result;
    }

    public List<Notification> findAllByUserId(String userId) {
        List<Notification> result = new ArrayList<>();

        table().scan().items()
            .forEach(n -> {
                String storedUserId = String.valueOf(n.getUserId());

                if (userId.trim().equals(storedUserId.trim())) {
                    result.add(n);
                }
            });
        return result;
    }

    public void softDeleteAll(String userId) {
        List<Notification> list = findByUserId(userId);
        for (Notification n : list) {
            n.softDelete();
            table().updateItem(n);
        }
    }

    public void saveAll(List<Notification> notifications) {
        for (Notification notification : notifications) {
            save(notification);
        }
    }
}

