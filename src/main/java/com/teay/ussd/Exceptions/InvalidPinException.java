package com.teay.ussd.Exceptions;

public class InvalidPinException extends Exception {
    public InvalidPinException(String message){
        super(message);
    }
}
