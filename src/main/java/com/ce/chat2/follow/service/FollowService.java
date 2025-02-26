package com.ce.chat2.follow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.follow.exception.AlreadyFollowingException;
import com.ce.chat2.follow.exception.FollowNotFoundException;
import com.ce.chat2.follow.repository.FollowRepository;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;

    public List<Follow> getFollow(User currentUser) {
        List<Follow> friends = new ArrayList<>();
        followRepository.getFollowsByFrom(currentUser).forEach(friends::add);
        return friends;
    }

    public void setFollow(User currentUser, User friend) {
        // 친구 목록에서 이미 친구인지 확인.
        Follow follow = followRepository.findByFromAndTo(currentUser, friend).orElse(null);

        if (follow != null) {
            throw new AlreadyFollowingException("이미 친구입니다.");
        }
        follow = Follow.builder().from(currentUser).to(friend).isBreak(false).build();
        followRepository.save(follow);
    }

    public void deleteFollow(User currentUser, User friend) {
        Follow follow = followRepository.findByFromAndTo(currentUser, friend)
                .orElseThrow(() -> new FollowNotFoundException("친구 관계가 아닙니다."));

        follow.setIsBreak(true);
        followRepository.save(follow);
    }

    public List<User> findFollow(User currentUser, String name) {
        // 현재 사용자의 친구 목록을 가져오기. -> repository로 변경
        List<Follow> follows = getFollow(currentUser);

        // 친구 목록에서 이름이 일치하는 친구 찾기. -> 처음부터 쿼리로 찾는 것이 더 효율적.
        List<User> friends = new ArrayList<>();
        for (Follow follow : follows) {
            if (follow.getTo().getName().contains(name)) {
                friends.add(follow.getTo());
            }
        }

        // 만약 users가 비어있다면 친구를 찾지 못한 것이므로 에러를 반환.
        if (friends.isEmpty()) {
            throw new USER_NOT_FOUND("친구를 찾을 수 없습니다.");
        }

        return friends;
    }
}
