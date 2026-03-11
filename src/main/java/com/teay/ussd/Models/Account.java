package com.teay.ussd.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name="accounts")
public class Account {
    @Id
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "hashed_pin", nullable = false)
    private String hashedPin;

    @Column(name="balance", nullable = false, precision = 19, scale =4)
    private BigDecimal balance;

    public Account(){}
    public Account(String phoneNumber, String hashedPin, BigDecimal balance) {
        this.phoneNumber = phoneNumber;
        this.hashedPin = hashedPin;
        this.balance = balance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public void setHashedPin(String hashedPin) {
        this.hashedPin = hashedPin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
