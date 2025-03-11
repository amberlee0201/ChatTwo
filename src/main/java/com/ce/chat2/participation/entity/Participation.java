package com.ce.chat2.participation.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;

@ToString
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Participation {

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("UserId"), @DynamoDbSecondarySortKey(indexNames = { "RoomId-UserId-index" })})
    private Integer userId;

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("RoomId"), @DynamoDbSecondaryPartitionKey(indexNames = { "RoomId-UserId-index", "RoomId-lastReadChatTime-index" })})
    private String roomId;

    @Getter(onMethod_ = {@DynamoDbAttribute("InvitedAt")})
    private Instant invitedAt;

    @Getter(onMethod_ = {@DynamoDbAttribute("InvitedBy")})
    private Integer invitedBy;

    @Getter(onMethod_ = {@DynamoDbAttribute("lastReadChatTime"), @DynamoDbSecondarySortKey(indexNames = { "RoomId-lastReadChatTime-index" })})
    private long lastReadChatTime;

    public static Participation of(Integer userId, String roomId, Integer invitedBy) {
        Instant now = Instant.now();
        return Participation.builder()
                .userId(userId)
                .roomId(roomId)
                .invitedAt(now)
                .invitedBy(invitedBy)
                .build();
    }

    public static Participation of(Integer userId, String roomId) {
        return Participation.builder()
                .userId(userId)
                .roomId(roomId)
                .build();
    }

    public Participation updateLastReadChatTime(long chatTime){
        this.lastReadChatTime = chatTime;
        return this;
    }
}
