package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.dto.request.RoomInviteRequest;
import com.ce.chat2.room.dto.request.RoomNameRequest;
import com.ce.chat2.room.service.RoomService;
import com.ce.chat2.user.dto.UserListResponse;
import com.ce.chat2.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;

    // 채팅방 신규 생성
    @PostMapping("/api/rooms")
    public ResponseEntity<Void> createChatRoom(@RequestBody RoomInviteRequest request, @AuthenticationPrincipal Oauth2UserDetails user) {
        User creator = user.getUser();
        roomService.createNewRoom(request, creator);
        return ResponseEntity.ok().build();
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
        roomService.addMembers(request, roomId, user.getUser());
        return ResponseEntity.ok().build();
    }

    // 채팅방 이름 변경
    @PutMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> updateRoomName(@PathVariable String roomId,
                                               @RequestBody RoomNameRequest request,
                                               @AuthenticationPrincipal Oauth2UserDetails user) {
        roomService.updateRoomName(request, roomId, request.getRoomName(), user.getUser());
        return ResponseEntity.ok().build();
    }

    // 채팅방 참여자 목록 조회
    @GetMapping("/api/rooms/{roomId}/participants")
    public ResponseEntity<List<UserListResponse>> getMembers(@PathVariable String roomId, @AuthenticationPrincipal Oauth2UserDetails user) {
        return ResponseEntity.ok(roomService.getMembers(roomId));
    }
}
