package com.ce.chat2.friend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.follow.exception.FollowNotFoundException;
import com.ce.chat2.follow.repository.FollowRepository;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.entity.UserRole;
import com.ce.chat2.user.exception.USER_NOT_FOUND;
import com.ce.chat2.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
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
public class ApiFriendsController {
        private final UserRepository userRepository;
        private final FollowRepository followRepository;

        private User getCurrentUser() {
                // 현재 로그인한 사용자 정보를 가져오는 메소드
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return ((Oauth2UserDetails) principal).getUser();
        }

        // 친구 목록을 가져오는 API
        @GetMapping("/api/friends")
        public ResponseEntity<List<Follow>> friendsList() {
                User currentUser = getCurrentUser();

                List<Follow> friends = new ArrayList<Follow>();
                followRepository.getFollowsByFrom(currentUser).forEach(friends::add);

                return ResponseEntity.ok(friends);
        }

        // 친구 추가 API
        @PostMapping("/api/friends/{uid}")
        public ResponseEntity<User> addFriend(@PathVariable("uid") Integer uid) {
                User currentUser = getCurrentUser();

                User friend = userRepository.findById(uid).orElseThrow(() -> new USER_NOT_FOUND("User not found"));

                Follow follow = Follow.builder().from(currentUser).to(friend).isBreak(false).build();
                followRepository.save(follow);

                return ResponseEntity.ok(friend);
        }

        // 친구 삭제 API
        @DeleteMapping("/api/friends/{uid}")
        public ResponseEntity<User> deleteFriend(@PathVariable("uid") Integer uid) {
                User currentUser = getCurrentUser();

                User friend = userRepository.findById(uid).orElseThrow(() -> new USER_NOT_FOUND("User not found"));

                Follow follow = followRepository.findByFromAndTo(currentUser, friend)
                                .orElseThrow(() -> new FollowNotFoundException("Follow relationship not found"));

                followRepository.delete(follow);

                return ResponseEntity.ok(friend);
        }

        // 친구 찾기 API
        @GetMapping("/api/friends/find")
        public ResponseEntity<List<User>> findFriend(@RequestPart("name") String name) {
                User currentUser = getCurrentUser();

                // 현재 사용자의 친구 목록을 가져오기.
                List<Follow> friends = new ArrayList<Follow>();
                followRepository.getFollowsByFrom(currentUser).forEach(friends::add);

                // 친구 목록에서 이름이 일치하는 친구 찾기.
                List<User> users = new ArrayList<User>();
                for (Follow friend : friends) {
                        if (friend.getTo().getName().contains(name)) {
                                users.add(friend.getTo());
                        }
                }

                // 만약 users가 비어있다면 친구를 찾지 못한 것이므로 에러를 반환.
                if (users.isEmpty()) {
                        throw new USER_NOT_FOUND("친구를 찾을 수 없습니다.");
                }

                return ResponseEntity.ok(users);
        }

}
