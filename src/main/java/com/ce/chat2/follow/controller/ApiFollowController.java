package com.ce.chat2.follow.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.follow.service.FollowService;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.USER_NOT_FOUND;
import com.ce.chat2.user.repository.UserRepository;

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
        private final UserRepository userRepository;

        // 친구 목록을 가져오는 API - dto 반환하도록 변경할 것.
        @GetMapping("/api/friends/{uid}")
        public ResponseEntity<List<Follow>> getFollow(@AuthenticationPrincipal Oauth2UserDetails userDetails) {
                // uid와 userDetails의 uid가 일치하는지 확인하는 코드
                // if (uid != userDetails.getUid()) {
                // throw new IllegalArgumentException("uid와 userDetails의 uid가 일치하지 않습니다.");

                List<Follow> friends = followService.getFollow(userDetails.getUser());
                return ResponseEntity.ok(friends);
        }

        // 친구 추가 API - dto 반환하도록 변경할 것.
        @PostMapping("/api/friends/{uid}")
        public ResponseEntity<User> setFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {
                User friend = userRepository.findById(uid).orElseThrow(() -> new USER_NOT_FOUND("User not found"));

                followService.setFollow(userDetails.getUser(), friend);

                return ResponseEntity.ok(friend);
        }

        // 친구 삭제 API - dto 반환하도록 변경할 것.
        @DeleteMapping("/api/friends/{uid}")
        public ResponseEntity<User> deleteFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {

                User friend = userRepository.findById(uid).orElseThrow(() -> new USER_NOT_FOUND("User not found"));

                followService.deleteFollow(userDetails.getUser(), friend);

                return ResponseEntity.ok(friend);
        }

        // 친구 찾기 API - dto 반환하도록 변경할 것.
        @GetMapping("/api/friends/find")
        public ResponseEntity<List<User>> findFollow(@RequestParam("name") String name,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {

                List<User> users = followService.findFollow(userDetails.getUser(), name);

                return ResponseEntity.ok(users);
        }

}
