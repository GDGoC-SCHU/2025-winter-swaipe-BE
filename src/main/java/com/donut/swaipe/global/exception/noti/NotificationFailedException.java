package com.donut.swaipe.global.exception.noti;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;

public class NotificationFailedException extends CustomException {

	public NotificationFailedException() {
		super(MessageCode.NOTIFICATION_SEND_FAILED);
	}
}