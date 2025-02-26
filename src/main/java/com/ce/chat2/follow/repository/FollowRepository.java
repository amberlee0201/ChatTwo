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

    @Query("SELECT f FROM Follow f WHERE f.from = :from AND f.isBreak = false")
    List<Follow> getFollowsByFrom(User from);

    Optional<Follow> findByFromAndTo(User currentUser, User friend);

    @Query("SELECT f.to FROM Follow f WHERE f.from = :currentUser AND f.to.name = :name AND f.isBreak = false")
    List<User> findFollowsByUserAndName(@Param("currentUser") User currentUser, @Param("name") String name);

}