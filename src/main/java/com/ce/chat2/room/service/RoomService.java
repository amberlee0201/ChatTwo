package com.ce.chat2.room.service;

import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.exception.NoFriendsFoundException;
import com.ce.chat2.room.repository.RoomRepository;
import com.ce.chat2.user.dto.UserListResponse;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public List<String> sendInitialRooms(Integer userId) {
        return participationRepository.findAllRoomsByUserId(userId)
                .stream()
                .map(Participation::getRoomId)
                .collect(Collectors.toList());
    }

    /**
     * TODO TransactWriteItems 사용 검토
     * Fallback 처리 (트랜잭션 실패 시)
     * 1. 트랜잭션 실패 시 재시도 (Exponential Backoff)
     *    try-catch 블록에서 실패 감지 후, 일정 시간 후 재시도
     *    AWS SDK의 내장 재시도 메커니즘을 활용할 수도 있음
     * 2. SQS를 활용한 비동기 재처리
     *    실패 시 DynamoDB에 직접 다시 쓰는 것이 아니라, SQS에 메시지를 보내고 백그라운드에서 재시도하는 방식도 가능
     */
    public Room createNewRoom(User creator, List<Integer> allMembersId) {
        // 1. uuid 생성, latestMessage 생성
        UUID uuid = UUID.randomUUID();
        Integer creatorId = creator.getId();
        String roomId = uuid.toString();
        String roomName = creator.getName() + " 님의 채팅방";
        String latestMessage = "[System] 새로운 채팅방이 생성되었습니다.";
        Instant now = Instant.now();

        Room newRoom = Room.builder()
                .roomId(roomId)
                .roomName(roomName)
                .latestMessage(latestMessage)
                .createdAt(now)
                .latestTimestamp(now)
                .build();

        // 2. ChatRoom 테이블에 데이터 생성
        roomRepository.save(newRoom);

        // 3. uuid와 user 매핑해서 batch insert
        List<Participation> newParticipations = allMembersId.stream().map(id ->
                        Participation.builder()
                                .roomId(roomId)
                                .userId(id)
                                .invitedAt(now)
                                .invitedBy(creatorId)
                                .build())
                .collect(Collectors.toList());

        participationRepository.batchSave(newParticipations);

        return newRoom;
    }

    public void exitRoom(Integer userId, String roomId) {
        // delete user to room relation
        Participation userToExit = Participation.builder().userId(userId).roomId(roomId).build();
        participationRepository.delete(userToExit);
    }

    public List<UserListResponse> getFriendsListNotInChatRoom(User user, String roomId) {

        Set<Integer> members = participationRepository.findUserIdsByRoomId(roomId);

        List<UserListResponse> responses = userRepository.findByFrom(user)
                .stream()
                .filter(f -> !members.contains(f.getId()))
                .map(UserListResponse::to)
                .collect(Collectors.toList());

        if (responses.isEmpty()) {
            throw new NoFriendsFoundException();
        }
        return responses;
    }

    public void addMembers(String roomId, List<Integer> invitedIds, User inviter){

        Instant now = Instant.now();
        Integer inviterId = inviter.getId();

        List<Participation> newParticipations = invitedIds.stream().map(id ->
                        Participation.builder()
                                .roomId(roomId)
                                .userId(id)
                                .invitedAt(now)
                                .invitedBy(inviterId)
                                .build())
                .collect(Collectors.toList());

        participationRepository.batchSave(newParticipations);
    }

    public Room updateRoomName(String roomId, String roomName, User updater) {

        // 예시 메시지
        String latestMessage = "[System] " + updater.getName() + " 님이 채팅방 이름을 변경했습니다.";

        Room roomWithNewName = Room.builder()
                .roomId(roomId)
                .roomName(roomName)
                .latestMessage(latestMessage)
                .build();

        return roomRepository.update(roomWithNewName);
    }
}
