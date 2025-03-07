package com.ce.chat2.room.listener;

import com.ce.chat2.room.dto.response.NewRoomResponse;
import com.ce.chat2.room.dto.response.RoomListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${redis.topic.user-participation}")
    private String redisTopic;
    @Value("${websocket.destination.prefix.user}")
    private String userDestPrefix;

    public void publish(String msg){
        stringRedisTemplate.convertAndSend(redisTopic, msg);
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
                messagingTemplate.convertAndSend(userDestPrefix + userId, roomListResponse);
            }
        } catch (Exception e) {
            log.error("Error processing user room notification", e);
        }
    }
}
