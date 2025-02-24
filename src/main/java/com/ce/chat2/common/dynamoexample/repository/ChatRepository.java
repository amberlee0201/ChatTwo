package com.ce.chat2.common.dynamoexample.repository;

import com.ce.chat2.common.dynamoexample.entity.Chat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
public class ChatRepository {

    private final DynamoDbTable<Chat> table;

    public ChatRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                          @Value("${aws.dynamodb.table-name}") String tableName) {
        this.table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Chat.class));
    }

    public void save(Chat chat) {
        table.putItem(chat);
    }

    public Optional<Chat> findChatByRoomIdAndMessageId(String roomId, String messageId) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(roomId).sortValue(messageId).build()));
    }

    public List<Chat> findByRoomId(String roomId) {
        return table.query(request -> request.queryConditional(
                QueryConditional.keyEqualTo(key -> key.partitionValue(roomId))
        )).items().stream().collect(Collectors.toList());
    }

//    private final DynamoDbClient dynamoDbClient;
//    private final String tableName;
//
//    public ChatRepository(DynamoDbClient dynamoDbClient, @Value("${aws.dynamodb.table-name}") String tableName) {
//        this.dynamoDbClient = dynamoDbClient;
//        this.tableName = tableName;
//    }
//
//    public void saveMessage(Chat message) {
//        PutItemRequest putItemRequest = PutItemRequest.builder()
//                .tableName(tableName)
//                .item(Map.of(
//                        "room_id", AttributeValue.builder().s(message.getRoomId()).build(),
//                        "message_id", AttributeValue.builder().s(message.getMessageId()).build(),
//                        "user_id", AttributeValue.builder().s(message.getUserId()).build(),
//                        "content", AttributeValue.builder().s(message.getContent()).build(),
//                        "timestamp", AttributeValue.builder().s(message.getTimestamp()).build()
//                ))
//                .build();
//
//        dynamoDbClient.putItem(putItemRequest);
//    }
//
//    public Optional<Chat> getMessage(String roomId, String messageId) {
//        GetItemRequest getItemRequest = GetItemRequest.builder()
//                .tableName(tableName)
//                .key(Map.of(
//                        "room_id", AttributeValue.builder().s(roomId).build(),
//                        "message_id", AttributeValue.builder().s(messageId).build()
//                ))
//                .build();
//
//        Map<String, AttributeValue> item = dynamoDbClient.getItem(getItemRequest).item();
//
//        if (item.isEmpty()) {
//            return Optional.empty();
//        }
//
//        return Optional.of(new Chat(
//                item.get("room_id").s(),
//                item.get("message_id").s(),
//                item.get("user_id").s(),
//                item.get("content").s(),
//                item.get("timestamp").s())
//        );
//    }
}
