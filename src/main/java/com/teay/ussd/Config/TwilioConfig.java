package com.teay.ussd.Config;

import com.twilio.Twilio;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    private final EnvConfig envConfig;

    public TwilioConfig(EnvConfig envConfig) {
        this.envConfig = envConfig;
        Twilio.init(
                envConfig.getAccountSid(),
                envConfig.getAuthToken()
        );
    }


}
