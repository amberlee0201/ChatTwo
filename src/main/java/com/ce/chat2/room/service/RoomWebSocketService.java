package com.ce.chat2.room.service;


import com.ce.chat2.room.dto.response.NewRoomResponse;
import com.ce.chat2.room.dto.response.RoomListResponse;
import com.ce.chat2.room.dto.response.RoomResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.exception.RoomNotFoundException;
import com.ce.chat2.room.listener.ParticipationMessageListener;
import com.ce.chat2.room.listener.RoomMessageListener;
import com.ce.chat2.room.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomWebSocketService {

    private final RoomRepository roomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ParticipationMessageListener participationMessageListener;
    private final RoomMessageListener roomMessageListener;
    private final ObjectMapper objectMapper;

    @Value("${websocket.destination.prefix.room}")
    private String roomDestPrefix;
    @Value("${websocket.destination.prefix.user}")
    private String userDestPrefix;

    // @TODO event source origin
    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination(); // 구독한 토픽 주소

        if (destination != null && destination.startsWith(roomDestPrefix)) {
            String roomId = destination.substring(roomDestPrefix.length());
            Room room = roomRepository.findRoomById(roomId);
            if (room != null) {
                // 채팅방 정보 전송
                RoomResponse response = RoomResponse.of(room);
                messagingTemplate.convertAndSend(destination, response);
            } else {
                throw new RoomNotFoundException();
            }
        }
    }

    @Async
    public void sendInitialRoom(Integer userId, RoomListResponse response){
        messagingTemplate.convertAndSend(userDestPrefix + userId, response);
    }

    @Async
    public void notifyUsersAboutNewRoom(NewRoomResponse response) {
        try {
            String message = objectMapper.writeValueAsString(response);
            participationMessageListener.publish(message);
        } catch (JsonProcessingException e) {
            log.error("Error converting notification to JSON", e);
        }
    }

    @Async
    public void notifyUsersAboutUpdatedRoom(RoomResponse response) {
        try {
            String message = objectMapper.writeValueAsString(response);
            roomMessageListener.publish(message);
        } catch (JsonProcessingException e) {
            log.error("Error converting room update to JSON", e);
        }
    }
}
