package com.ce.chat2.chat.repository;

import com.ce.chat2.chat.entity.Chat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class ChatRepository {

    private final DynamoDbTable<Chat> chatTable;

    public ChatRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
        @Value("${cloud.aws.dynamodb.table-name.chat}") String chatTableName) {
        this.chatTable = dynamoDbEnhancedClient.table(chatTableName, TableSchema.fromBean(Chat.class));
    }
    public List<Chat> findAllByRoomId(String roomId){
        return chatTable.query(QueryConditional.keyEqualTo(k -> k.partitionValue(roomId)))
            .items().stream().collect(Collectors.toList());
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
