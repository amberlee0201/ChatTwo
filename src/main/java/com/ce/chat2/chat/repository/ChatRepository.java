package com.ce.chat2.chat.repository;

import com.ce.chat2.chat.entity.Chat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class ChatRepository {

    private final DynamoDbTable<Chat> chatTable;
    private final DynamoDbIndex<Chat> chatTableGsiWithCreatedAt;

    public ChatRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
        @Value("${cloud.aws.dynamodb.table-name.chat}") String chatTableName,
        @Value("${cloud.aws.dynamodb.index.chat-gsi}") String chatTableGsiWithCreatedAt
    ) {
        this.chatTable = dynamoDbEnhancedClient.table(chatTableName, TableSchema.fromBean(Chat.class));
        this.chatTableGsiWithCreatedAt = chatTable.index(chatTableGsiWithCreatedAt);
    }
    public List<Chat> findAllByRoomIdSortByCreatedAtDesc(String roomId) {
        return chatTableGsiWithCreatedAt.query(r -> r
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(roomId)))
                .scanIndexForward(false) // 내림차순 정렬 (최신순)
            ).stream()
            .flatMap(page -> page.items().stream())
            .collect(Collectors.toList());
    }

    public Chat save(Chat chat) {
        chatTable.putItem(chat);
        return chat;
    }

    public void update(Chat chat) {
        chatTable.updateItem(chat);
    }

    public void delete(Chat chat) {
        chatTable.deleteItem(chat);
    }
}
