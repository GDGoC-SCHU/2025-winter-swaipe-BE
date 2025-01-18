package com.donut.swab.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckDto {
    private boolean available;
    private String message;
}