package com.ce.chat2.chat.entity;

import com.ce.chat2.chat.dto.request.ChatRequestDto;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@ToString
public class Chat {

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("RoomId"), @DynamoDbSecondaryPartitionKey(indexNames = { "RoomId-CreatedAt-index" })})
    private String roomId;

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("ChatId")})
    private String chatId;

    @Getter(onMethod_ = {@DynamoDbAttribute("SenderId")})
    private Integer senderId;

    @Getter(onMethod_ = {@DynamoDbAttribute("Content")})
    private String content;

    @Getter(onMethod_ = {@DynamoDbAttribute("FilePath")})
    private String filePath;

    @Getter(onMethod_ = {@DynamoDbAttribute("FileType")})
    private String fileType;

    @Getter(onMethod_ = {@DynamoDbAttribute("CreatedAt"), @DynamoDbSecondarySortKey(indexNames = { "RoomId-CreatedAt-index" })})
    private Instant createdAt;

    public static Chat of(ChatRequestDto chatRequestDto){
        Instant now = Instant.now();
        String chatId = now + "#" + UUID.randomUUID();
        return Chat.builder()
            .roomId(chatRequestDto.getRoomId())
            .chatId(chatId)
            .senderId(chatRequestDto.getUserId())
            .content(chatRequestDto.getContent())
            .filePath(chatRequestDto.getFilePath())
            .fileType(chatRequestDto.getFileType())
            .createdAt(now)
            .build();
    }

    public boolean isEqualOrAfter(long lastReadTime){
        return lastReadTime >= this.createdAt.toEpochMilli(); // lastReadTime가 더 늦거나 같으면 true
    }

    public String timeToString(){
        return this.createdAt.toString(); // return "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    }
}
