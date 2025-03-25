package com.ce.chat2.follow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.user.entity.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

    // 친구 추가/삭제를 위한 검색
    Optional<Follow> findByFromAndTo(User from, User to);
}