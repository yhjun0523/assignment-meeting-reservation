package com.yhjun.meetingreservation.repository;

import com.yhjun.meetingreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}