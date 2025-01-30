package com.donut.swaipe.global.exception.noti;

import com.donut.swaipe.global.common.MessageCode;
import com.donut.swaipe.global.exception.CustomException;

public class NotificationNotFoundException extends CustomException {
    public NotificationNotFoundException() {
        super(MessageCode.NOTIFICATION_NOT_FOUND);
    }
}
