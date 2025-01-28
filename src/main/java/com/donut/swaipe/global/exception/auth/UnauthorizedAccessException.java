package com.donut.swaipe.global.exception.auth;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;

// global/exception/user/UnauthorizedAccessException.java
public class UnauthorizedAccessException extends CustomException {

	public UnauthorizedAccessException() {
		super(MessageCode.UNAUTHORIZED_ACCESS);
	}
}