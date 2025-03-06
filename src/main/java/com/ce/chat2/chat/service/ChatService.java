package com.ce.chat2.chat.service;

import com.ce.chat2.chat.dto.ChatRoomDto;
import com.ce.chat2.chat.dto.request.ChatRequestDto;
import com.ce.chat2.chat.dto.response.ChatResponseDto;
import com.ce.chat2.chat.entity.Chat;
import com.ce.chat2.chat.repository.ChatRepository;
import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.exception.RoomNotFound;
import com.ce.chat2.room.repository.RoomRepository;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final RoomRepository roomRepository;
    private final ParticipationRepository participationRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatRoomDto getChatHistory(String roomId) {
        roomRepository.findRoomById(roomId).orElseThrow(RoomNotFound::new);

        List<Participation> allByRoomId = participationRepository.findAllByRoomId(roomId);
        List<User> participants = new ArrayList<>();
        allByRoomId.forEach(p -> participants.add(userRepository.findById(p.getUserId()).orElseThrow(UserNotFound::new)));

        List<Chat> chats = chatRepository.findAllByRoomId(roomId);
        List<ChatResponseDto> chatResponseList = new ArrayList<>();

        int idx = 0;
        int cnt = allByRoomId.size();

        for(Chat c : chats){
            while (idx < allByRoomId.size() && c.compareTime(allByRoomId.get(idx).getLastReadChatTime())) {
                cnt--;
                idx++;
            }
            User u = userRepository.findById(c.getSenderId()).orElseThrow(UserNotFound::new); // TODO 쿼리 최적화 Participation에 user를 넣을지 논의
            chatResponseList.add(ChatResponseDto.of(c, u, cnt));
        }
        return ChatRoomDto.of(chatResponseList, participants);
    }

    public String saveChat(ChatRequestDto chatRequest) {
        return chatRepository.save(Chat.of(chatRequest)).getChatId();
    }
}
