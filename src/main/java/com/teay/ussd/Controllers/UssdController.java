package com.teay.ussd.Controllers;

import com.teay.ussd.Services.UssdService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ussd")
public class UssdController {
    private final UssdService ussdService;


    public UssdController(UssdService ussdService) {
        this.ussdService = ussdService;
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleUssd(
            @RequestParam String sessionId,
            @RequestParam String serviceCode,
            @RequestParam String phoneNumber,
            @RequestParam(defaultValue = "") String text)
    {
        return ussdService.processInput(sessionId,phoneNumber,text);
    }

}
