package com.donut.swab.global.exception.user;

import com.donut.swab.global.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicateNicknameException extends CustomException {
    public DuplicateNicknameException() {
        super("이미 존재하는 nickname입니다.");
    }
}
