package com.ce.chat2.room.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {

    @GetMapping("/rooms")
    public String room(@AuthenticationPrincipal Oauth2UserDetails oAuth2User,
                       Model model) {
        model.addAttribute("user", oAuth2User.getUser());
        return "main";
    }
}
