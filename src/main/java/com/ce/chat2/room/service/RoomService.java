package com.ce.chat2.room.service;

import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import com.ce.chat2.room.dto.request.RoomInviteRequest;
import com.ce.chat2.room.dto.request.RoomNameRequest;
import com.ce.chat2.room.dto.response.NewRoomResponse;
import com.ce.chat2.room.dto.response.RoomListResponse;
import com.ce.chat2.room.dto.response.RoomResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.exception.NoFriendsFoundException;
import com.ce.chat2.room.repository.RoomRepository;
import com.ce.chat2.user.dto.UserListResponse;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.repository.UserRepository;
import com.ce.chat2.room.exception.NoMembersFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final RoomWebSocketService roomWebSocketService;

    public void sendInitialRooms(Integer userId) {
        RoomListResponse response = RoomListResponse.of(participationRepository.findAllRoomsByUserId(userId)
                .stream()
                .map(Participation::getRoomId)
                .collect(Collectors.toList()));

        roomWebSocketService.sendInitialRoom(userId, response);
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
    public void createNewRoom(RoomInviteRequest request, User creator) {
        List<Integer> invitedIds = request.getInvitedIds();
        Integer creatorId = creator.getId();
        List<Integer> allMembersId = Stream.concat(invitedIds.stream(), Stream.of(creatorId)).collect(Collectors.toList());
        String latestMessage = "[System] 새로운 채팅방이 생성되었습니다.";

        Room newRoom = roomRepository.save(Room.of(creator, latestMessage));
        List<Participation> invitedParticipants = invite(allMembersId, newRoom.getRoomId(), creatorId);
// ✅ 중복 userId 제거 (LinkedHashMap으로 순서 보존 + 유일 key 보장)
        List<Participation> deduplicated = new ArrayList<>(
            invitedParticipants.stream()
                .collect(Collectors.toMap(
                    Participation::getUserId,  // userId 기준
                    p -> p,
                    (existing, replacement) -> existing, // 중복일 때 기존 값 유지
                    LinkedHashMap::new
                )).values()
        );
       // participationRepository.batchSave(invite(allMembersId, newRoom.getRoomId(), creator.getId()));
        participationRepository.batchSave(deduplicated); // ✅ 중복 제거된 리스트 저장


        NewRoomResponse response = NewRoomResponse.of(newRoom.getRoomId(), allMembersId);
        roomWebSocketService.notifyUsersAboutNewRoom(response);
    }

    public void exitRoom(Integer userId, String roomId) {
        participationRepository.delete(Participation.of(userId, roomId));
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

    public void addMembers(RoomInviteRequest request, String roomId, User inviter) {
        List<Integer> invitedIds = request.getInvitedIds();

        participationRepository.batchSave(invite(invitedIds, roomId, inviter.getId()));
        NewRoomResponse response = NewRoomResponse.of(roomId, invitedIds);

        roomWebSocketService.notifyUsersAboutNewRoom(response);
    }

    public void updateRoomName(RoomNameRequest request, String roomId, String roomName, User updater) {
        String latestMessage = "[System] " + updater.getName() + " 님이 채팅방 이름을 변경했습니다.";
        Room updatedRoom = roomRepository.update(Room.of(roomId, roomName, latestMessage));
        RoomResponse response = RoomResponse.of(updatedRoom);
        roomWebSocketService.notifyUsersAboutUpdatedRoom(response);
    }

    private List<Participation> invite(List<Integer> invited, String roomId, Integer inviterId) {
        return invited.stream()
                .map(id -> Participation.of(id, roomId, inviterId))
                .collect(Collectors.toList());
    }
    /**
     * 채팅방 참여자의 목록 조회
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 참여자를 표현하는 UserListResponse List
     * @throws NoMembersFoundException 채팅방에 참여자가 없을 때 예외 발생
     */
    public List<UserListResponse> getMembers(String roomId) {

        Set<Integer> members = participationRepository.findUserIdsByRoomId(roomId);

        List<UserListResponse> responses = userRepository.findAll()
                .stream()
                .filter(f -> members.contains(f.getId()))
                .map(UserListResponse::to)
                .collect(Collectors.toList());
        if (responses.isEmpty()) {
            throw new NoMembersFoundException();
        }
        return responses;
    }
}
