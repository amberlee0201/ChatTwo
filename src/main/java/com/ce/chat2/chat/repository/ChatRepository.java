package com.ce.chat2.chat.repository;

import com.ce.chat2.chat.entity.Chat;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
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
    public Optional<Page<Chat>> findAllByRoomIdSortByCreatedAtDesc(String roomId, Map<String, AttributeValue> lastKey) {
        SdkIterable<Page<Chat>> res = chatTableGsiWithCreatedAt.query(r -> r
            .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(roomId)))
            .scanIndexForward(false) // 내림차순 정렬 (최신순)
            .limit(20)
            .exclusiveStartKey(lastKey)
        );
        return res.stream().findFirst();
    }
    public Optional<Page<Chat>> findAllByRoomIdSortByCreatedAtDesc(String roomId) {
        return findAllByRoomIdSortByCreatedAtDesc(roomId, null);
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
