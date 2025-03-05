package com.ce.chat2.room.service;


import com.ce.chat2.room.dto.response.NewRoomResponse;
import com.ce.chat2.room.dto.response.RoomResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.listener.ParticipationMessageListener;
import com.ce.chat2.room.listener.RoomMessageListener;
import com.ce.chat2.room.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    private final ParticipationMessageListener participationMessageListener;
    private final RoomMessageListener roomMessageListener;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final String ROOM_DEST_PREFIX = "/room-sub/room/";

    // @TODO event source origin
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
        try {
            NewRoomResponse response = NewRoomResponse.builder()
                    .userIds(userIds)
                    .roomId(roomId)
                    .build();
            String message = objectMapper.writeValueAsString(response);
            participationMessageListener.publish(message);
        } catch (JsonProcessingException e) {
            log.error("Error converting notification to JSON", e);
        }
    }

    @Async
    public void updateRoom(Room room) {
        try {
            RoomResponse response = RoomResponse.of(room);
            String message = objectMapper.writeValueAsString(response);
            roomMessageListener.publish(message);
        } catch (JsonProcessingException e) {
            log.error("Error converting room update to JSON", e);
        }
    }
}
