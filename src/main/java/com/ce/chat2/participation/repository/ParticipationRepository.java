package com.ce.chat2.participation.repository;

import com.ce.chat2.participation.entity.Participation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ParticipationRepository {


    private final DynamoDbEnhancedClient enhancedClient;

    private final DynamoDbTable<Participation> participationTable;
    private final DynamoDbIndex<Participation> participationGsi;

    public ParticipationRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                   @Value("${cloud.aws.dynamodb.table-name.participation}") String participationTableName,
                                   @Value("${cloud.aws.dynamodb.index.participation-gsi}") String participationGsi) {
        this.enhancedClient = dynamoDbEnhancedClient;
        this.participationTable = enhancedClient.table(participationTableName,
                TableSchema.fromBean(Participation.class));
        this.participationGsi = participationTable.index(participationGsi);
    }

    public List<Participation> findAllRoomsByUserId(Integer userId) {
        return participationTable.query(QueryConditional.keyEqualTo(key -> key.partitionValue(userId).build()))
                .items().stream().collect(Collectors.toList());
    }

    public void save(Participation participation) {
        participationTable.putItem(participation);
    }

    public void batchSave(List<Participation> participationList) {
        final int BATCH_SIZE = 25; // DynamoDB batch write limit

        List<WriteBatch> writeBatches = new ArrayList<>();

        for (int i = 0; i < participationList.size(); i += BATCH_SIZE) {
            List<Participation> chunk = participationList.subList(i, Math.min(i + BATCH_SIZE, participationList.size()));

            WriteBatch.Builder<Participation> batchBuilder = WriteBatch.builder(Participation.class)
                    .mappedTableResource(participationTable);

            for (Participation participation : chunk) {
                batchBuilder.addPutItem(b -> b.item(participation));
            }

            writeBatches.add(batchBuilder.build());
            enhancedClient.batchWriteItem(r -> r.writeBatches(writeBatches));
            writeBatches.clear();
        }
    }

    public void delete(Participation participation) {
        participationTable.deleteItem(participation);
    }

    public Set<Integer> findUserIdsByRoomId(String roomId) {

        Set<Integer> userIds = new HashSet<>();
        participationGsi.query(QueryConditional.keyEqualTo(key -> key.partitionValue(roomId).build()))
                .stream().forEach(page -> page.items()
                .forEach(p -> {
                    userIds.add(p.getUserId());
                }));
        return userIds;
    }
}
