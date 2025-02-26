package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.room.dto.CreateRoomRequest;
import com.ce.chat2.room.dto.ExitRoomRequest;
import com.ce.chat2.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String room(@AuthenticationPrincipal Oauth2UserDetails oAuth2User,
                            Model model) {
        model.addAttribute("user", oAuth2User.getUser());
        return "room/room-list";
    }

    @MessageMapping("/rooms/create/{userId}")
    public void createNewRoom(@DestinationVariable String userId, @Payload CreateRoomRequest request) {
        log.info("create new room: {}", request.getUserIds());

        roomService.createNewRoom(userId, request.getUserIds());
    }

    @MessageMapping("/rooms/exit")
    public void exitRoom(@Payload ExitRoomRequest request) {
        log.info("exit room: {}", request.getRoomId());

        roomService.exitRoom(request.getUserId(), request.getRoomId());
    }

}
