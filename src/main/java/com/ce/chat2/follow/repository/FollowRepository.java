package com.ce.chat2.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ce.chat2.follow.entity.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

}