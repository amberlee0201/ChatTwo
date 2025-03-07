package com.ce.chat2.chat.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Setter
@DynamoDbBean
public class ChatFile {
    @Getter(onMethod_ = {@DynamoDbAttribute("FileUrl")})
    private String fileUrl;

    @Getter(onMethod_ = {@DynamoDbAttribute("OriginalFileName")})
    private String originalFileName;
}
