package com.donut.swab.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.donut.swab.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	@Modifying
	@Query("DELETE FROM User u WHERE u.username = :username")
	int deleteByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByNickname(String nickname);
}
