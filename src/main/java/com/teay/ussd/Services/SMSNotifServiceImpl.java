package com.teay.ussd.Services;

import com.teay.ussd.Config.EnvConfig;
import com.teay.ussd.Config.TwilioConfig;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;


@Service
public class SMSNotifServiceImpl implements SMSNotifService{
    private final EnvConfig envConfig;
    private final TwilioConfig twilioConfig;

    public SMSNotifServiceImpl(EnvConfig envConfig, TwilioConfig twilioConfig) {
        this.envConfig = envConfig;
        this.twilioConfig = twilioConfig;
    }

    @Override
    public Object sendSMS(String phoneNumer, String message) throws Exception {
        try{
            if(phoneNumer != null ){
                String cleanedPhoneNumber = phoneNumer.replaceAll("[^0-9]", "");
                if(cleanedPhoneNumber.startsWith("0") && cleanedPhoneNumber.length() ==11){
                    cleanedPhoneNumber = "234"+cleanedPhoneNumber.substring(1);
                }
                String cleanedPhoneNumberWithSym = cleanedPhoneNumber.startsWith("+")? cleanedPhoneNumber : "+" + cleanedPhoneNumber;
                PhoneNumber to = new PhoneNumber(cleanedPhoneNumberWithSym);
                PhoneNumber from = new PhoneNumber(envConfig.getPhoneNumber());

                return Message.creator(to,from,message).create();
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
