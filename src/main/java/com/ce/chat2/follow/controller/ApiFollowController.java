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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequiredArgsConstructor
public class ApiFollowController {
        private final FollowService followService;
        private final UserRepository userRepository;

        private User getCurrentUser() {
                // 현재 로그인한 사용자 정보를 가져오는 메소드
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return ((Oauth2UserDetails) principal).getUser();
        }

        // 친구 목록을 가져오는 API
        @GetMapping("/api/friends")
        public ResponseEntity<List<Follow>> getFollow() {
                User currentUser = getCurrentUser();
                List<Follow> friends = followService.getFollow(currentUser);
                return ResponseEntity.ok(friends);
        }

        // 친구 추가 API
        @PostMapping("/api/friends/{uid}")
        public ResponseEntity<User> setFollow(@PathVariable("uid") Integer uid) {
                User currentUser = getCurrentUser();

                User friend = userRepository.findById(uid).orElseThrow(() -> new USER_NOT_FOUND("User not found"));

                followService.setFollow(currentUser, friend);

                return ResponseEntity.ok(friend);
        }

        // 친구 삭제 API
        @DeleteMapping("/api/friends/{uid}")
        public ResponseEntity<User> deleteFollow(@PathVariable("uid") Integer uid) {
                User currentUser = getCurrentUser();

                User friend = userRepository.findById(uid).orElseThrow(() -> new USER_NOT_FOUND("User not found"));

                followService.deleteFollow(currentUser, friend);

                return ResponseEntity.ok(friend);
        }

        // 친구 찾기 API
        @GetMapping("/api/friends/find")
        public ResponseEntity<List<User>> findFollow(@RequestPart("name") String name) {
                User currentUser = getCurrentUser();

                List<User> users = followService.findFollow(currentUser, name);

                return ResponseEntity.ok(users);
        }

}
