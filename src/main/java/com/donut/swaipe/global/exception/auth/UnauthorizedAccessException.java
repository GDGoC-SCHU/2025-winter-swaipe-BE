package com.donut.swaipe.global.exception.auth;

import com.donut.swaipe.global.exception.CustomException;

// global/exception/user/UnauthorizedAccessException.java
public class UnauthorizedAccessException extends CustomException {

	public UnauthorizedAccessException() {
		super("해당 작업을 수행할 권한이 없습니다.");
	}
}