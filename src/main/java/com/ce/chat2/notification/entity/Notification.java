// file path: com/ce/chat2/notification/entity/Notification.java

package com.ce.chat2.notification.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Notification {

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("UserId")})
    private String userId;

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("CreatedAt")})
    private Long createdAt;

    @Getter(onMethod_ = {@DynamoDbAttribute("NotificationId")})
    private String notificationId;

    @Getter(onMethod_ = {@DynamoDbAttribute("Subject")})
    private String subject;

    @Getter(onMethod_ = {@DynamoDbAttribute("Message")})
    private String message;

    @Getter(onMethod_ = {@DynamoDbAttribute("IsDeleted")})
    private Boolean isDeleted;

    @Getter(onMethod_ = {@DynamoDbAttribute("DeletedAt")})
    private Instant deletedAt;

    @DynamoDbAttribute("IsDeleted")
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @DynamoDbAttribute("DeletedAt")
    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public static Notification of(String userId, String subject, String message) {
        Instant now = Instant.now();
        return Notification.builder()
            .userId(userId)
            .createdAt(now.toEpochMilli())
            .notificationId(UUID.randomUUID().toString())
            .subject(subject)
            .message(message)
            .isDeleted(false)
            .build();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
