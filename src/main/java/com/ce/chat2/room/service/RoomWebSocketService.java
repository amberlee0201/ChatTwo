package com.ce.chat2.room.service;


import com.ce.chat2.room.dto.RoomListResponse;
import com.ce.chat2.room.dto.RoomResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomWebSocketService {

    private final RoomRepository roomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    private static final String ROOM_DEST_PREFIX = "/room-sub/room/";
    private static final String USER_DEST_PREFIX = "/room-sub/user/";

    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination(); // 구독한 토픽 주소
        log.info("SessionSubscribeEvent - destination = {}", destination);

        if (destination != null && destination.startsWith(ROOM_DEST_PREFIX)) {
            String roomId = destination.substring(ROOM_DEST_PREFIX.length());
            log.info("roomId={}", roomId);
            // DB에서 해당 채팅방 정보 조회
            Room room = roomRepository.findRoomById(roomId);

            if (room != null) {
                // 채팅방 정보 전송
                RoomResponse response = RoomResponse.of(room);
                messagingTemplate.convertAndSend(destination, response);
                log.info("Subscribed to room {}. Sent room info: {}", roomId, response);
            }
        }
    }

    @Async
    public void notifyUsersAboutNewRoom(List<Integer> userIds, String roomId) {
        RoomListResponse listResponse = RoomListResponse.builder()
                .rooms(List.of(roomId))
                .build();
        userIds.forEach(id -> messagingTemplate.convertAndSend(USER_DEST_PREFIX + id, listResponse));
    }

    @Async
    public void updateRoom(String roomId, Room room) {
        String destination = ROOM_DEST_PREFIX + roomId;
        RoomResponse response = RoomResponse.of(room);
        messagingTemplate.convertAndSend(destination, response);
    }
}
