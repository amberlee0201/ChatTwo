package com.ce.chat2.room.listener;

import com.ce.chat2.room.dto.response.RoomResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${redis.topic.room-update}")
    private String redisTopic;
    @Value("${websocket.destination.prefix.room}")
    private String roomDestPrefix;

    public void publish(String msg){
        stringRedisTemplate.convertAndSend(redisTopic, msg);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());

            RoomResponse response = objectMapper.readValue(payload, RoomResponse.class);
            String destination = roomDestPrefix + response.getRoomId();
            messagingTemplate.convertAndSend(destination, response);
        } catch (Exception e) {
            log.error("Error processing room update message", e);
        }
    }
}
