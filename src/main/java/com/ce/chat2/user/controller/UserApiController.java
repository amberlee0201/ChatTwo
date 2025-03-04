package com.ce.chat2.user.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.user.dto.UserFindResponse;
import com.ce.chat2.user.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    // 유저를 검색하는 API
    @GetMapping("")
    public ResponseEntity<List<UserFindResponse>> findUser(@RequestParam("name") String name) {
        return ResponseEntity.ok(userService.findUser(name));
    }
}
