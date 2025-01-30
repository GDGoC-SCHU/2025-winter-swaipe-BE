package com.donut.swaipe.global.exception.noti;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;

public class NotificationAccessDeniedException extends CustomException {

	public NotificationAccessDeniedException() {
		super(MessageCode.NOTIFICATION_ACCESS_DENIED);
	}
}
