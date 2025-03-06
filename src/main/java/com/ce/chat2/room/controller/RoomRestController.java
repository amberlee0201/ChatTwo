package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.dto.request.RoomInviteRequest;
import com.ce.chat2.room.dto.request.RoomNameRequest;
import com.ce.chat2.room.dto.response.RoomInviteResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.service.RoomService;
import com.ce.chat2.room.service.RoomWebSocketService;
import com.ce.chat2.user.dto.UserListResponse;
import com.ce.chat2.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;
    private final RoomWebSocketService roomWebSocketService;

    // 채팅방 신규 생성
    @PostMapping("/api/rooms")
    public ResponseEntity<RoomInviteResponse> createChatRoom(@RequestBody RoomInviteRequest request, @AuthenticationPrincipal Oauth2UserDetails user) {
        List<Integer> invitedIds = request.getInvitedIds();
        User creator = user.getUser();
        Integer creatorId = creator.getId();

        List<Integer> allMembersId = Stream.concat(invitedIds.stream(), Stream.of(creatorId)).collect(Collectors.toList());
        Room newRoom = roomService.createNewRoom(creator, allMembersId);

        // send websocket msg async
        roomWebSocketService.notifyUsersAboutNewRoom(allMembersId, newRoom.getRoomId());

        // @TODO
        RoomInviteResponse response = RoomInviteResponse.of(newRoom.getRoomId());
        return ResponseEntity.ok(response);
    }

    // 채팅방 퇴장
    @DeleteMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> exitChatRoom(@PathVariable String roomId, @AuthenticationPrincipal Oauth2UserDetails user) {
        roomService.exitRoom(user.getUser().getId(), roomId);

        return ResponseEntity.ok().build();
    }

    // 해당 채팅방에 없는 친구 목록
    @GetMapping("/api/rooms/{roomId}/friends")
    public ResponseEntity<List<UserListResponse>> getFriendsListNotInChatRoom(@PathVariable String roomId, @AuthenticationPrincipal Oauth2UserDetails user) {
        return ResponseEntity.ok(roomService.getFriendsListNotInChatRoom(user.getUser(), roomId));
    }

    // 채팅방 유저 추가
    @PostMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> addMembers(@PathVariable String roomId,
                                           @RequestBody RoomInviteRequest request,
                                           @AuthenticationPrincipal Oauth2UserDetails user) {
        List<Integer> invitedIds = request.getInvitedIds();

        roomService.addMembers(roomId, invitedIds, user.getUser());

        // @TODO
        roomWebSocketService.notifyUsersAboutNewRoom(invitedIds, roomId);
        return ResponseEntity.ok().build();
    }

    // 채팅방 이름 변경
    @PutMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> updateRoomName(@PathVariable String roomId,
                                               @RequestBody RoomNameRequest request,
                                               @AuthenticationPrincipal Oauth2UserDetails user) {
        Room room = roomService.updateRoomName(roomId, request.getRoomName(), user.getUser());

        roomWebSocketService.updateRoom(room);
        return ResponseEntity.ok().build();
    }

}
