package com.ce.chat2.chat.service;

import com.ce.chat2.chat.dto.ChatRoomDto;
import com.ce.chat2.chat.dto.request.ChatHistoryRequestDto;
import com.ce.chat2.chat.dto.request.ChatRequestDto;
import com.ce.chat2.chat.dto.response.ChatPageResponseDto;
import com.ce.chat2.chat.dto.response.ChatResponseDto;
import com.ce.chat2.chat.entity.Chat;
import com.ce.chat2.chat.repository.ChatRepository;
import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.exception.RoomNotFoundException;
import com.ce.chat2.room.repository.RoomRepository;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final RoomRepository roomRepository;
    private final ParticipationRepository participationRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatRoomDto getChatHistory(String roomId, int userId) {
        roomRepository.findRoomById(roomId).orElseThrow(RoomNotFoundException::new);

        List<Participation> allByRoomId = participationRepository.findAllByRoomIdSortByLastReadChatTimeDesc(roomId);

        List<User> participants = new ArrayList<>();
        allByRoomId.forEach(p -> participants.add(userRepository.findById(p.getUserId()).orElseThrow(UserNotFound::new)));
        Optional<Page<Chat>> optionalChatPage = chatRepository.findAllByRoomIdSortByCreatedAtDesc(roomId);

        if(optionalChatPage.isEmpty()) return null;

        List<Chat> chats = optionalChatPage.get().items();

        List<ChatResponseDto> chatResponseList = getChatResponseDtoList(allByRoomId, chats);
        return ChatRoomDto.of(chatResponseList, participants,convertLastEvaluatedKey(optionalChatPage.get()
            .lastEvaluatedKey()));
    }

    public ChatPageResponseDto getChatPage(ChatHistoryRequestDto chatHistoryRequestDto){
        roomRepository.findRoomById(chatHistoryRequestDto.getRoomId()).orElseThrow(RoomNotFoundException::new);
        List<Participation> allByRoomId = participationRepository.findAllByRoomIdSortByLastReadChatTimeDesc(
            chatHistoryRequestDto.getRoomId());
        Optional<Page<Chat>> optionalChatPage = chatRepository.findAllByRoomIdSortByCreatedAtDesc(
            chatHistoryRequestDto.getRoomId(), convertToAttributeValueMap(chatHistoryRequestDto.getLastKey()));
        if(optionalChatPage.isEmpty()) return null;

        List<Chat> chats = optionalChatPage.get().items();
        List<ChatResponseDto> chatResponseList = getChatResponseDtoList(allByRoomId, chats);

        return ChatPageResponseDto.of(chatResponseList, convertLastEvaluatedKey(optionalChatPage.get().lastEvaluatedKey()));
    }

    public String saveChat(ChatRequestDto chatRequest) {
        return chatRepository.save(Chat.of(chatRequest)).getChatId();
    }

    private List<ChatResponseDto> getChatResponseDtoList(List<Participation> allByRoomId,
        List<Chat> chats) {
        List<ChatResponseDto> chatResponseList = new ArrayList<>();
        int idx = 0;
        int cnt = allByRoomId.size();

        for (Chat c : chats) {
            while (cnt > 0 && idx < allByRoomId.size() && c.isEqualOrAfter(allByRoomId.get(idx).getLastReadChatTime())) {
                cnt--;
                idx++;
            }
            User u = userRepository.findById(c.getSenderId()).orElseThrow(UserNotFound::new);
            chatResponseList.add(ChatResponseDto.of(c, u, cnt));
        }
        return chatResponseList;
    }

    private Map<String, String> convertLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKey) {
        if (lastEvaluatedKey == null || lastEvaluatedKey.isEmpty()) {
            return Collections.emptyMap();
        }

        return lastEvaluatedKey.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().s()
            ));
    }
    private Map<String, AttributeValue> convertToAttributeValueMap(Map<String, String> stringMap) {
        return stringMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> AttributeValue.builder().s(entry.getValue()).build()
            ));
    }
}
