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
    List<Follow> findByFrom(User from);

    // 친구 추가/삭제를 위한 검색
    Optional<Follow> findByFromAndTo(User from, User to);

    // 친구 목록에서 친구를 찾아 친구 이름으로 검색, 조인 쿼리 사용
    @Query("SELECT f FROM Follow f JOIN f.to u WHERE f.from = :from AND u.name LIKE :name AND f.isBreak = false")
    List<Follow> findByFromAndName(@Param("from") User from, @Param("name") String name);

}