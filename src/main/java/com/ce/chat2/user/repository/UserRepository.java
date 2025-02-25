package com.ce.chat2.user.repository;

import com.ce.chat2.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByProviderId(String id);

    User findByName(String friendName);
}
