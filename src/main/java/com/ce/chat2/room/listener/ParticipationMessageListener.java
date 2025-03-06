package com.ce.chat2.room.listener;

import com.ce.chat2.room.dto.response.NewRoomResponse;
import com.ce.chat2.room.dto.response.RoomListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationMessageListener implements MessageListener {
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_TOPIC = "user:participation";
    private static final String USER_DEST_PREFIX = "/room-sub/user/";

    public void publish(String msg){
        stringRedisTemplate.convertAndSend(REDIS_TOPIC, msg);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            NewRoomResponse response = objectMapper.readValue(payload, NewRoomResponse.class);
            RoomListResponse roomListResponse = RoomListResponse.builder()
                    .rooms(Collections.singletonList(response.getRoomId()))
                    .build();

            for (Integer userId : response.getUserIds()) {
                messagingTemplate.convertAndSend(USER_DEST_PREFIX + userId, roomListResponse);
            }
        } catch (Exception e) {
            log.error("Error processing user room notification", e);
        }
    }
}
