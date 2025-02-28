package com.ce.chat2.follow.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.follow.service.FollowService;
import com.ce.chat2.user.dto.UserFindResponse;
import com.ce.chat2.user.dto.UserListResponse;
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

        // 친구 목록을 가져오는 API
        @GetMapping("/api/friends/{uid}")
        public ResponseEntity<List<UserListResponse>> getFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {
                // uid와 userDetails의 uid가 일치하는지 확인
                if (uid != userDetails.getUser().getId()) {
                        throw new UnAuthorizedUser();
                }
                System.out.println("친구 목록 가져오기");
                return ResponseEntity.ok(followService.getFollow(userDetails.getUser()));
        }

        // 친구 추가 API
        @PostMapping("/api/friends/{uid}")
        public ResponseEntity<String> setFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {
                System.out.println("친구 추가");
                followService.setFollow(userDetails.getUser(), uid);
                return ResponseEntity.ok("친구 추가 성공");
        }

        // 친구 삭제 API
        @DeleteMapping("/api/friends/{uid}")
        public ResponseEntity<String> deleteFollow(@PathVariable("uid") Integer uid,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {
                System.out.println("친구 삭제");
                followService.deleteFollow(userDetails.getUser(), uid);
                return ResponseEntity.ok("친구 삭제 성공");
        }

        // 친구 찾기 API
        @GetMapping("/api/friends/find")
        public ResponseEntity<List<UserFindResponse>> findFollow(@RequestParam("name") String name,
                        @AuthenticationPrincipal Oauth2UserDetails userDetails) {
                System.out.println("친구 찾기");
                return ResponseEntity.ok(followService.findFollow(userDetails.getUser(), name));
        }

}
