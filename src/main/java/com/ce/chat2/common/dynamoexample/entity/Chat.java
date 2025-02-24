package com.ce.chat2.common.dynamoexample.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@ToString
public class Chat {
    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("room_id")})
    private String roomId;

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("message_id")})
    private String messageId;

    @Getter(onMethod_ = @DynamoDbAttribute("user_id"))
    private String userId;

    @Getter(onMethod_ = @DynamoDbAttribute("content"))
    private String content;

    @Getter(onMethod_ = @DynamoDbAttribute("timestamp")) // @TODO change to Number type
    private String timestamp;
}
