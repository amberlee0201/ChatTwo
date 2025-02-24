package com.ce.chat2.friend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FriendController {

    @GetMapping("/friends")
    String friendsList() {
        return "friendsList";
    }
}