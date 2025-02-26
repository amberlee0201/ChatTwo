package com.ce.chat2.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.user.entity.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

    // 현재 친구 목록
    @Query("SELECT f FROM Follow f WHERE f.from = :from AND f.isBreak = false")
    List<Follow> findFollowsByFrom(User from);

    // 친구 추가/삭제를 위한 검색
    Optional<Follow> findByFromAndTo(User from, User to);

    // 친구 검색
    @Query("SELECT f FROM Follow f WHERE f.from = :from AND f.to.name LIKE :name AND f.isBreak = false")
    List<Follow> findFollowsByUserAndName(@Param("from") User from, @Param("name") String name);

}