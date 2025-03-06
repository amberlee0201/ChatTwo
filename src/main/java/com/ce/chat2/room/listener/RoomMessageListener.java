package com.ce.chat2.room.listener;

import com.ce.chat2.room.dto.response.RoomResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomMessageListener implements MessageListener {
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_TOPIC = "room:update";
    private static final String ROOM_DEST_PREFIX = "/room-sub/room/";

    public void publish(String msg){
        stringRedisTemplate.convertAndSend(REDIS_TOPIC, msg);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());

            RoomResponse response = objectMapper.readValue(payload, RoomResponse.class);
            String destination = ROOM_DEST_PREFIX + response.getRoomId();
            messagingTemplate.convertAndSend(destination, response);
        } catch (Exception e) {
            log.error("Error processing room update message", e);
        }
    }
}
