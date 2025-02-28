package com.ce.chat2.follow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.follow.exception.AlreadyFollowingException;
import com.ce.chat2.follow.exception.FollowNotFoundException;
import com.ce.chat2.follow.repository.FollowRepository;
import com.ce.chat2.user.dto.UserFindResponse;
import com.ce.chat2.user.dto.UserListResponse;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public List<UserListResponse> getFollow(User currentUser) {

        return userRepository.findByFrom(currentUser).stream()
                .map(UserListResponse::to).collect(Collectors.toList());
    }

    public void setFollow(User currentUser, Integer uid) {

        // 추가할 친구가 존재하는지 확인.
        User friend = userRepository.findById(uid).orElseThrow(() -> new UserNotFound());

        // 친구 목록에 있나 확인.
        Follow follow = followRepository.findByFromAndTo(currentUser, friend).orElse(null);

        if (follow != null && !follow.isBreak()) {
            // 친구 목록에 있으며 isBreak가 false이면
            throw new AlreadyFollowingException();
        } else if (follow != null) {
            // 친구 목록에 있으며 isBreak가 true이면
            follow.updateBreak(false);
        } else {
            // 친구 목록에 없으면 추가
            follow = Follow.builder().from(currentUser).to(friend).isBreak(false).build();
        }

        followRepository.save(follow);
    }

    public void deleteFollow(User currentUser, Integer uid) {
        // 추가할 친구가 존재하는지 확인.
        User friend = userRepository.findById(uid).orElseThrow(() -> new UserNotFound());

        // 친구 목록에 있나 확인.
        Follow follow = followRepository.findByFromAndTo(currentUser, friend)
                .orElseThrow(() -> new FollowNotFoundException());

        follow.updateBreak(true);
        followRepository.save(follow);
    }

    public List<UserFindResponse> findFollow(User currentUser, String name) {

        List<UserFindResponse> friends = userRepository.findByFromAndName(currentUser, "%" + name + "%").stream()
                .map(UserFindResponse::to).collect(Collectors.toList());

        if (friends.isEmpty()) {
            throw new UserNotFound();
        }

        return friends;
    }
}
