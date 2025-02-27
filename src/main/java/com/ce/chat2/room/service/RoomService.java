package com.ce.chat2.room.service;

import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.time.Instant;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ParticipationRepository participationRepository;

    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination(); // 구독한 토픽 주소
        String prefix = "/topic/room/";
        log.info("SessionSubscribeEvent - destination = {}", destination);

        if (destination != null && destination.startsWith(prefix)) {
            String roomId = destination.substring(prefix.length());
            log.info("roomId={}", roomId);
            // DB에서 해당 채팅방 정보 조회
            Room room = roomRepository.findRoomById(roomId);

            if (room != null) {
                // 채팅방 정보 전송
                simpMessageSendingOperations.convertAndSend(destination, room);
                log.info("Subscribed to room {}. Sent room info: {}", roomId, room);
            }
        }
    }

    /**
     * TODO TransactWriteItems 사용 검토
     * Fallback 처리 (트랜잭션 실패 시)
     * 1. 트랜잭션 실패 시 재시도 (Exponential Backoff)
     *    try-catch 블록에서 실패 감지 후, 일정 시간 후 재시도
     *    AWS SDK의 내장 재시도 메커니즘을 활용할 수도 있음
     * 2. SQS를 활용한 비동기 재처리
     *    실패 시 DynamoDB에 직접 다시 쓰는 것이 아니라, SQS에 메시지를 보내고 백그라운드에서 재시도하는 방식도 가능
     * */
    public Room createNewRoom(String creatorId, List<String> userIds) {
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
        userIds.add(creatorId);
        List<Participation> newParticipations = userIds.stream().map(id ->
                Participation.builder()
                        .roomId(roomId)
                        .userId(id)
                        .createdAt(now)
                        .build())
                .toList();

        log.info(String.valueOf(newParticipations.size()));
        participationRepository.batchSave(newParticipations);

        // 4. event 각각의 유저들에게 publish
        String prefix = "/topic/user/";
        userIds.forEach(id -> simpMessageSendingOperations.convertAndSend(prefix + id, Collections.singletonList(roomId)));

        return newRoom;
    }

    public void exitRoom(String userId, String roomId) {
        // delete user to room relation
        Participation userToExit = Participation.builder().userId(userId).roomId(roomId).build();
        participationRepository.delete(userToExit);
    }
}
