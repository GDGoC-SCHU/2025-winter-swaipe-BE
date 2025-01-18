package com.donut.swab.global.exception.user;

import com.donut.swab.global.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicateUsernameException extends CustomException {
    public DuplicateUsernameException() {
        super("이미 존재하는 username입니다.");
    }
}