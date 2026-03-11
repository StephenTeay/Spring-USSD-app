package com.teay.ussd.Services;

public interface SMSNotifService {
    Object sendSMS(String phoneNumer, String message) throws Exception;
}
