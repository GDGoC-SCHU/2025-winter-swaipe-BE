package com.donut.swab.domain.user.dto;

import com.donut.swab.domain.user.enums.UserRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private Long id;
    private String username;
    private String nickname;
}