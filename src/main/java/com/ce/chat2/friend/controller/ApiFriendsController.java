package com.ce.chat2.friend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.follow.repository.FollowRepository;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.entity.UserRole;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
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

        // 친구 목록을 가져오는 API
        @GetMapping("/api/friends")
        public ResponseEntity<List<Follow>> friendsList() {

                // 현재 로그인한 유저를 찾는 기능 필요.
                User currentUser = userRepository.findById(1).get();

                List<Follow> friends = new ArrayList<Follow>();
                followRepository.getFollowsByFrom(currentUser).forEach(friends::add);

                return ResponseEntity.ok(friends);
        }

        // 친구 추가 API
        @PostMapping("/api/friends/{uid}")
        public ResponseEntity<User> addFriend(@PathVariable("uid") Integer uid) {
                // 현재 로그인한 유저를 찾는 기능 필요.
                User currentUser = userRepository.findById(1).get();

                User friend = userRepository.findById(uid).orElseThrow(UserNotFound::new);

                Follow follow = Follow.builder().from(currentUser).to(friend).isBreak(false).build();
                followRepository.save(follow);

                return ResponseEntity.ok(friend);
        }

        // 친구 삭제 API
        @DeleteMapping("/api/friends/{uid}")
        public ResponseEntity<User> deleteFriend(@PathVariable("uid") Integer uid) {
                // 현재 로그인한 유저를 찾는 기능 필요.
                User currentUser = userRepository.findById(1).get();

                User friend = userRepository.findById(uid).orElseThrow(UserNotFound::new);

                Follow follow = followRepository.findByFromAndTo(currentUser, friend);
                if (follow == null) {
                        return ResponseEntity.notFound().build();
                }

                followRepository.delete(follow);

                return ResponseEntity.ok(friend);
        }

}
