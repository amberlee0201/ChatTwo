package com.ce.chat2.room.repository;

import com.ce.chat2.room.entity.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;


@Repository
public class RoomRepository {

    private final DynamoDbTable<Room> roomTable;

    public RoomRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                          @Value("${cloud.aws.dynamodb.table-name.chat-room}") String chatRoomTableName) {
        this.roomTable = dynamoDbEnhancedClient.table(chatRoomTableName, TableSchema.fromBean(Room.class));
    }

    public Room findRoomById(String roomId) {
        return roomTable.getItem(Key.builder().partitionValue(roomId).build());
    }

    public Room save(Room room) {
        roomTable.putItem(room);
        return room;
    }

    public Room update(Room room) {
        return roomTable.updateItem(room);
    }

    public void delete(Room room) {
        roomTable.deleteItem(room);
    }

}
