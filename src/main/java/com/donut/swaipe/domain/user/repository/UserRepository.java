package com.donut.swaipe.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.donut.swaipe.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	// 기본 JPA 메서드는 그대로 유지
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByNickname(String nickname);
	Optional<User> findByFcmToken(String fcmToken);
}

