package com.donut.swaipe.domain.kakao.repository;

import com.donut.swaipe.domain.kakao.entity.KakaoUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {

	Optional<KakaoUser> findByKakaoId(String kakaoId);
}