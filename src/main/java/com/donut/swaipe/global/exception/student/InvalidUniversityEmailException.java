package com.donut.swaipe.global.exception.student;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;

public class InvalidUniversityEmailException extends CustomException {
    public InvalidUniversityEmailException() {
        super(MessageCode.INVALID_UNIVERSITY_EMAIL);
    }
} 