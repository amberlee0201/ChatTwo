package com.ce.chat2.user.repository;

import com.ce.chat2.follow.entity.Follow;
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

    User findByName(String name);

    List<User> findByNameContaining(String namePart);

    // 현재 친구 목록
    @Query("SELECT u FROM User u JOIN Follow f on f.to = u WHERE f.from = :fromUser AND f.isBreak = false")
    List<Follow> findByFrom(User fromUser);

    // 친구 추가/삭제를 위한 검색
    Optional<Follow> findByFromAndTo(User from, User to);

    // 친구 목록에서 친구를 찾아 친구 이름으로 검색, 조인 쿼리 사용 -> 유저 먼저 읽도록 수정하자.
    @Query("SELECT u FROM User u JOIN Follow f on f.to = u WHERE f.from = :fromUser AND u.name LIKE :name AND f.isBreak = false")
    List<Follow> findByFromAndName(@Param("from") User fromUser, @Param("name") String name);
}
