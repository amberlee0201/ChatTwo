package com.ce.chat2.room.service;

import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipationRepository participationRepository;

    public List<String> sendInitialRooms(Integer userId) {
        List<String> roomList = participationRepository.findAllRoomsByUserId(userId)
                .stream()
                .map(Participation::getRoomId)
                .toList();
        log.info("roomList: {}", roomList);
        return roomList;
    }

    /**
     * TODO TransactWriteItems 사용 검토
     * Fallback 처리 (트랜잭션 실패 시)
     * 1. 트랜잭션 실패 시 재시도 (Exponential Backoff)
     *    try-catch 블록에서 실패 감지 후, 일정 시간 후 재시도
     *    AWS SDK의 내장 재시도 메커니즘을 활용할 수도 있음
     * 2. SQS를 활용한 비동기 재처리
     *    실패 시 DynamoDB에 직접 다시 쓰는 것이 아니라, SQS에 메시지를 보내고 백그라운드에서 재시도하는 방식도 가능
     */
    public Room createNewRoom(Integer creatorId, List<Integer> allMembersId) {
        // 1. uuid 생성, latestMessage 생성
        UUID uuid = UUID.randomUUID();
        String roomId = uuid.toString();
        String roomName = creatorId + " 님의 채팅방"; // @TODO username
        String latestMessage = "[System] 새로운 채팅방이 생성되었습니다.";
        Instant now = Instant.now();

        Room newRoom = Room.builder()
                .roomId(roomId)
                .roomName(roomName)
                .latestMessage(latestMessage)
                .createdAt(now)
                .latestTimestamp(now)
                .build();

        log.info(newRoom.toString());

        // 2. ChatRoom 테이블에 데이터 생성
        roomRepository.save(newRoom);

        // 3. uuid와 user 매핑해서 batch insert
        List<Participation> newParticipations = allMembersId.stream().map(id ->
                        Participation.builder()
                                .roomId(roomId)
                                .userId(id)
                                .createdAt(now)
                                .build())
                .toList();

        log.info("{}", newParticipations.size());
        participationRepository.batchSave(newParticipations);

        return newRoom;
    }

    public void exitRoom(Integer userId, String roomId) {
        // delete user to room relation
        Participation userToExit = Participation.builder().userId(userId).roomId(roomId).build();
        participationRepository.delete(userToExit);
    }
}
