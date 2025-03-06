package com.ce.chat2.chat.service;

import com.ce.chat2.chat.dto.request.ReadCountRequestDto;
import com.ce.chat2.chat.dto.response.ReadCountResponseDto;
import com.ce.chat2.chat.exception.ChatSendException;
import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.exception.ParticipationNotFound;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.exception.RoomNotFoundException;
import com.ce.chat2.room.repository.RoomRepository;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadCountService implements MessageListener{
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ParticipationRepository participationRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public void publish(String channel, String msg){
        stringRedisTemplate.convertAndSend(channel, msg);
    }

    @Override
    @Transactional
    public void onMessage(Message msg, byte[] pattern){
        String payload = new String(msg.getBody());
        ObjectMapper om = new ObjectMapper();
        try{
            ReadCountRequestDto dto = om.readValue(payload, ReadCountRequestDto.class);
            roomRepository.findRoomById(dto.getRoomId()).orElseThrow(RoomNotFoundException::new);
            User user = userRepository.findById(dto.getUserId()).orElseThrow(UserNotFound::new);
            Participation participation = participationRepository.findByUserIdAndRoomId(user.getId(), dto.getRoomId())
                .orElseThrow(ParticipationNotFound::new);

            long firstReadChatTime = participation.getLastReadChatTime();
            long lastReadChatTime = dto.getChatTime();
            participationRepository.updateItem(participation.updateLastReadChatTime(dto.getChatTime()));

            simpMessageSendingOperations.convertAndSend("/chat-sub/count/"+dto.getRoomId(),
                ReadCountResponseDto.of(user.getId(), firstReadChatTime+1, lastReadChatTime));
        }catch (JsonProcessingException e){
            throw new ChatSendException(e.getMessage());
        }
    }
}
