package com.teay.ussd.Services;

import com.teay.ussd.Config.EnvConfig;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


public class SMSNotifServiceImpl implements SMSNotifService{
    private final EnvConfig envConfig;

    public SMSNotifServiceImpl(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

    @Override
    public Object sendSMS(String phoneNumer, String message) throws Exception {
        try{
            if(phoneNumer != null){
                PhoneNumber to = new PhoneNumber(phoneNumer);
                PhoneNumber from = new PhoneNumber(envConfig.getPhoneNumber());

                return Message.creator(to,from,message);
            }
            else{
                throw new IllegalArgumentException(
                        "Phone number [" + phoneNumer + "] is not valid"
                );
            }
        }
        catch (ApiException exception){
            throw new Exception(exception.getMessage());
        }
    }
}
