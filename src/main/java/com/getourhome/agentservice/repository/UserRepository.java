package com.getourhome.agentservice.repository;

import com.getourhome.agentservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByRegistrationNumber(String registrationNumber);
//    Optional<User> findById(UUID uuid);
}
