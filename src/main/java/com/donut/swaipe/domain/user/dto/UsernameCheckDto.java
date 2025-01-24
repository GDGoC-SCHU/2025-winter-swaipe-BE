package com.donut.swaipe.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsernameCheckDto {
    private boolean available;
    private String message;
}