package com.ce.chat2.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.user.entity.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

    Iterable<Follow> getFollowsByFrom(User from);

    Follow findByFromAndTo(User currentUser, User friend);

}