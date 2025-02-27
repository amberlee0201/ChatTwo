package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/room")
    public String room(@AuthenticationPrincipal Oauth2UserDetails oAuth2User,
                            Model model) {
        model.addAttribute("user", oAuth2User.getUser());
        return "room/room-list";
    }

    // 채팅방 신규 생성
    @PostMapping("/api/rooms")
    public ResponseEntity<String> createChatRoom(@RequestBody List<String> invited, @AuthenticationPrincipal Oauth2UserDetails user) {
        log.info("invited {} friends, userId: {}", invited.size(), user.getUser().getId());
        Room newRoom = roomService.createNewRoom(String.valueOf(user.getUser().getId()), invited);

        return ResponseEntity.ok(newRoom.getRoomId());
    }

    // 채팅방 퇴장
    @DeleteMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String roomId, @AuthenticationPrincipal Oauth2UserDetails user) {
        log.info("roomId: {}, userId: {}", roomId, user.getUser().getId());
        roomService.exitRoom(String.valueOf(user.getUser().getId()), roomId);

        return ResponseEntity.ok().build();
    }

}
