package com.ce.chat2.room.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedTimestampAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@ToString
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Room {

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("RoomId")})
    private String roomId;

    @Getter(onMethod_ = {@DynamoDbAttribute("RoomName")})
    private String roomName;

    @Getter(onMethod_ = {@DynamoDbAttribute("LatestMessage")})
    private String latestMessage;

    @Getter(onMethod_ = {@DynamoDbAttribute("LatestTimestamp"), @DynamoDbAutoGeneratedTimestampAttribute})
    private Instant latestTimestamp;

    @Getter(onMethod_ = {@DynamoDbAttribute("CreatedAt")})
    private Instant createdAt;

}
