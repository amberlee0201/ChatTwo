package com.ce.chat2.follow.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ce.chat2.common.oauth.Oauth2UserDetails;

@Controller
public class FollowController {

    @GetMapping("/friends")
    String friendsList(@AuthenticationPrincipal Oauth2UserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "followList";
    }

    @GetMapping("/searchFriend")
    String searchFriend(@AuthenticationPrincipal Oauth2UserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "searchFriend";
    }

}