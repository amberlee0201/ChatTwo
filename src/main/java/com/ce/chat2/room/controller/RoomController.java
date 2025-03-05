package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.dto.request.RoomInviteRequest;
import com.ce.chat2.room.dto.request.RoomNameRequest;
import com.ce.chat2.room.dto.response.RoomInviteResponse;
import com.ce.chat2.room.dto.response.RoomListResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.service.RoomService;
import com.ce.chat2.room.service.RoomWebSocketService;
import com.ce.chat2.user.dto.UserListResponse;
import com.ce.chat2.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomWebSocketService roomWebSocketService;
    private final SimpMessageSendingOperations messagingTemplate;

    @GetMapping("/rooms")
    public String room(@AuthenticationPrincipal Oauth2UserDetails oAuth2User,
                       Model model) {
        model.addAttribute("user", oAuth2User.getUser());
        return "main";
    }

    // 기존 채팅방 목록 요청 (처음 room-list 화면 로딩 시 최초 1회)
    @MessageMapping("/rooms/init")
    public void sendMessage(Authentication authentication) {

        if (authentication.getPrincipal() instanceof Oauth2UserDetails userDetails) {
            userDetails = (Oauth2UserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getUser().getId();
            RoomListResponse response = RoomListResponse.builder()
                    .rooms(roomService.sendInitialRooms(userId))
                    .build();
            messagingTemplate.convertAndSend("/room-sub/user/" + userId, response);
        } else {
            // @TODO
            throw new ClassCastException("authentication.getPrincipal() is not instanceof " + Oauth2UserDetails.class.getSimpleName());
        }
    }

    // 채팅방 신규 생성
    @PostMapping("/api/rooms")
    public ResponseEntity<RoomInviteResponse> createChatRoom(@RequestBody RoomInviteRequest request, @AuthenticationPrincipal Oauth2UserDetails user) {
        List<Integer> invitedIds = request.getInvitedIds();
        User creator = user.getUser();
        Integer creatorId = creator.getId();
        log.info("invited {} users, creatorId: {}", invitedIds.size(), creatorId);

        List<Integer> allMembersId = Stream.concat(invitedIds.stream(), Stream.of(creatorId)).toList();
        Room newRoom = roomService.createNewRoom(creator, allMembersId);

        // send websocket msg async
        roomWebSocketService.notifyUsersAboutNewRoom(allMembersId, newRoom.getRoomId());

        RoomInviteResponse createResponse = RoomInviteResponse.builder()
                .roomId(newRoom.getRoomId())
                .build();
        return ResponseEntity.ok(createResponse);
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
