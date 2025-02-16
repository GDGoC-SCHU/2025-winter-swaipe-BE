package com.donut.swaipe.domain.mms.service;

public interface SmsProvider {
    void sendSms(String to, String message);
}