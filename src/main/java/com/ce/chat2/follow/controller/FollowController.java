package com.ce.chat2.follow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FollowController {

    @GetMapping("/friends")
    String friendsList() {
        return "followList";
    }
}