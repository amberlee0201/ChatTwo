package com.ce.chat2.chat.service;

import com.ce.chat2.chat.dto.request.ChatRequestDto;
import com.ce.chat2.chat.dto.response.ChatResponseDto;
import com.ce.chat2.chat.entity.Chat;
import com.ce.chat2.chat.exception.ChatSendException;
import com.ce.chat2.chat.repository.ChatRepository;
import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.dto.response.RoomResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.exception.RoomNotFoundException;
import com.ce.chat2.room.repository.RoomRepository;
import com.ce.chat2.room.service.RoomService;
import com.ce.chat2.room.service.RoomWebSocketService;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
public class RedisChatPubSubService implements MessageListener {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final ParticipationRepository participationRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final RoomWebSocketService roomWebSocketService;
    private final RoomService roomService;

    public void publish(String channel, ChatRequestDto dto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Room room = roomRepository.findRoomById(dto.getRoomId()).orElseThrow(RoomNotFoundException::new);
        Chat chat = chatRepository.save(Chat.of(dto));
        updateRoom(room, chat);
        stringRedisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(dto));
    }

    @Override
    public void onMessage(Message msg, byte[] pattern){
        String payload = new String(msg.getBody());
        ObjectMapper om = new ObjectMapper();
        try{
            ChatRequestDto dto = om.readValue(payload, ChatRequestDto.class);
            User sender = userRepository.findById(dto.getUserId()).orElseThrow(UserNotFound::new);
            Chat chat = Chat.of(dto);

            roomService.updateLatestMessage(dto.getRoomId(), chat.getContent(), chat.getCreatedAt());

            List<Participation> participationList = participationRepository.findAllByRoomId(dto.getRoomId());
            simpMessageSendingOperations.convertAndSend("/chat-sub/"+dto.getRoomId(),
                ChatResponseDto.of(chat, sender, participationList.size()));
        }catch (JsonProcessingException e){
            throw new ChatSendException(e.getMessage());
        }
    }

    private void updateRoom(Room room, Chat chat){
        Room updatedRoom = roomRepository.save(Room.from(room, chat));
        roomWebSocketService.notifyUsersAboutUpdatedRoom(RoomResponse.of(updatedRoom));
    }
}
