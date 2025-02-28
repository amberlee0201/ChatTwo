package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.dto.RoomCreateRequest;
import com.ce.chat2.room.dto.RoomListResponse;
import com.ce.chat2.room.dto.RoomCreateResponse;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.service.RoomService;
import com.ce.chat2.room.service.RoomWebSocketService;
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
        return "room/room-list";
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
    public ResponseEntity<RoomCreateResponse> createChatRoom(@RequestBody RoomCreateRequest request, @AuthenticationPrincipal Oauth2UserDetails user) {
        List<Integer> invitedIds = request.getInvitedIds();
        Integer creatorId = user.getUser().getId();
        log.info("invited {} users, creatorId: {}", invitedIds.size(), creatorId);

        List<Integer> allMembersId = Stream.concat(invitedIds.stream(), Stream.of(creatorId)).toList();
        Room newRoom = roomService.createNewRoom(creatorId, allMembersId);

        // send websocket msg async
        roomWebSocketService.notifyUsersAboutNewRoom(allMembersId, newRoom.getRoomId());

        RoomCreateResponse createResponse = RoomCreateResponse.builder()
                .roomId(newRoom.getRoomId())
                .build();
        return ResponseEntity.ok(createResponse);
    }

    // 채팅방 퇴장
    @DeleteMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String roomId, @AuthenticationPrincipal Oauth2UserDetails user) {
        log.info("roomId: {}, userId: {}", roomId, user.getUser().getId());
        roomService.exitRoom(user.getUser().getId(), roomId);

        return ResponseEntity.ok().build();
    }

}
