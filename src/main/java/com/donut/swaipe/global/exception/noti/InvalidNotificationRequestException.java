package com.donut.swaipe.global.exception.noti;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;

public class InvalidNotificationRequestException extends CustomException {

	public InvalidNotificationRequestException() {
		super(MessageCode.INVALID_NOTIFICATION_REQUEST);
	}
}