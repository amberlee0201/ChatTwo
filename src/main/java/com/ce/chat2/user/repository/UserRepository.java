package com.ce.chat2.user.repository;

import com.ce.chat2.user.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByProviderId(String id);

    // 이름으로 사용자 찾기
    List<User> findByNameContaining(String name);

    // 현재 친구 목록
    @Query("SELECT u FROM User u JOIN Follow f on f.to = u WHERE f.from = :fromUser AND f.isBreak = false")
    List<User> findByFrom(@Param("fromUser") User fromUser);

    // 친구 목록에서 친구를 찾아 친구 이름으로 검색
    @Query("SELECT u FROM User u JOIN Follow f on f.to = u WHERE f.from = :fromUser AND u.name LIKE :name AND f.isBreak = false")
    List<User> findByFromAndName(@Param("fromUser") User fromUser, @Param("name") String name);
}
