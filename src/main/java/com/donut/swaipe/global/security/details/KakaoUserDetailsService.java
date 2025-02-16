package com.donut.swaipe.global.security.details;

import com.donut.swaipe.domain.kakao.entity.KakaoUser;
import com.donut.swaipe.domain.kakao.repository.KakaoUserRepository;
import com.donut.swaipe.global.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoUserDetailsService {
    
    private final KakaoUserRepository kakaoUserRepository;

    @Transactional(readOnly = true)
    public KakaoUserDetailsImpl loadUserByKakaoId(String kakaoId) {
        KakaoUser kakaoUser = kakaoUserRepository.findByKakaoId(kakaoId)
            .orElseThrow(UserNotFoundException::new);

        return new KakaoUserDetailsImpl(kakaoUser);
    }
}