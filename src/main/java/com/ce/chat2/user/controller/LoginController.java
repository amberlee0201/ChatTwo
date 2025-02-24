package com.ce.chat2.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/chats")
    String afterLogin(){
        return "main";
    }
}
