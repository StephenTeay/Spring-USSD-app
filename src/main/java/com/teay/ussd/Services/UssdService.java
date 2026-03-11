package com.teay.ussd.Services;

import com.teay.ussd.Exceptions.InvalidPinException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UssdService {
    private final AccountService accountService;
    private final SessionService sessionService;


    public UssdService(AccountService accountService, SessionService sessionService) {
        this.accountService = accountService;
        this.sessionService = sessionService;
    }

    public String processInput(String sessionId, String phoneNumber, String text) {
        if (text == null) text = "";
        text.trim();
        String[] parts = text.isEmpty() ? new String[0] : text.split("\\*", -1);
        int level = parts.length;
        if (level == 0) {
            sessionService.saveState(sessionId, "HOME");
            return "CON Welcome to Bank\n" +
                    "1. Create Account\n" +
                    "2. Deposit\n" +
                    "3. Withdraw\n" +
                    "4. Check Balance";
        }

        String choice = parts[0];
        if (level == 1) {
            switch (choice) {
                case "1":
                    return "CON Create Account\nEnter a 4-digit PIN:";
                case "2":
                    return "CON Deposit\nEnter amount to deposit:";
                case "3":
                    return "CON Withdraw\nEnter amount to Withdraw:";
                case "4":
                    return "CON Check Balance\nEnter your PIN:";
                default:
                    return "END Invalid option. Please try again.";
            }
        }

        if (level == 2) {
            String input2 = parts[1];
            switch (choice) {
                case "1": {
                    if (!isValidPin(input2)) {
                        return "END PIN must be exactly 4 digits. Please start over.";
                    }
                    try {
                        return accountService.createAccount(phoneNumber, input2);
                    } catch (RuntimeException e) {
                        return "END " + e.getMessage();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }


                case "2": {
                    BigDecimal amount = parseAmount(input2);
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        return "END Invalid amount. Please start over.";
                    }
                    try {
                        return accountService.deposit(phoneNumber, amount);
                    } catch (RuntimeException e) {
                        return "END " + e.getMessage();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "3": {
                    BigDecimal amount = parseAmount(input2);
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        return "END Invalid amount. Please start over.";
                    }
                    return "CON Enter your PIN to confirm withdrawal of " + amount + ":";
                }

                case "4": {
                    try {
                        BigDecimal balance = accountService.checkBalance(phoneNumber, input2);
                        sessionService.clearState(sessionId);
                        return String.valueOf(balance);

                    } catch (InvalidPinException e) {
                        return "END " + e.getMessage();
                    } catch (RuntimeException e) {
                        return "END " + e.getMessage();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                default:
                    return "END Invalid option";
            }
        }
        if(level == 3 && "3".equals(choice)){
            String amountStr = parts[1];
            String pin = parts[2];

            BigDecimal amount = parseAmount(amountStr);
            if(amount == null || amount.compareTo(BigDecimal.ZERO)<= 0){
                return "END Invalid amount. Please start over ";
            }
            try{
                String result = accountService.withdraw(phoneNumber, amount,pin);
                sessionService.clearState(sessionId);
                return result;
            }
            catch (InvalidPinException e){
                return "END "+e.getMessage();
            }
            catch(RuntimeException e){
                return "END "+e.getMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return "END Session error. Please try again.";
    }

    private boolean isValidPin(String pin){
        return pin != null && pin.matches("\\d{4}");
    }

    private BigDecimal parseAmount(String raw){
        try{
            return new BigDecimal(raw.trim());
        }
        catch(NumberFormatException e){
            return null;
        }
    }
}
