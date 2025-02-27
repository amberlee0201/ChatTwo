package com.ce.chat2.follow.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.follow.service.FollowService;
import com.ce.chat2.user.dto.UserResponse;
import com.ce.chat2.user.exception.UnAuthorizedUser;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class ApiFollowController {
        private final FollowService followService;

        // 친구 목록을 가져오는 API - dto 반환하도록 변경할 것.
        @GetMapping("/api/friends/{uid}")
        public ResponseEntity<List<UserResponse>> getFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {
                // uid와 userDetails의 uid가 일치하는지 확인
                if (uid != userDetails.getUser().getId()) {
                        throw new UnAuthorizedUser();
                }
                return ResponseEntity.ok(followService.getFollow(userDetails.getUser()));
        }

        // 친구 추가 API - dto 반환하도록 변경할 것.
        @PostMapping("/api/friends/{uid}")
        public ResponseEntity<UserResponse> setFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {

                return ResponseEntity.ok(followService.setFollow(userDetails.getUser(), uid));
        }

        // 친구 삭제 API - dto 반환하도록 변경할 것.
        @DeleteMapping("/api/friends/{uid}")
        public ResponseEntity<UserResponse> deleteFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {

                return ResponseEntity.ok(followService.deleteFollow(userDetails.getUser(), uid));
        }

        // 친구 찾기 API - dto 반환하도록 변경할 것.
        @GetMapping("/api/friends/find")
        public ResponseEntity<List<UserResponse>> findFollow(@RequestParam("name") String name,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {

                return ResponseEntity.ok(followService.findFollow(userDetails.getUser(), name));
        }

}
