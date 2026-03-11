package com.teay.ussd.Services;

import com.teay.ussd.Exceptions.InvalidPinException;
import com.teay.ussd.Models.Account;
import com.teay.ussd.Repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SMSNotifServiceImpl smsNotifService;

    public AccountService(AccountRepository accountRepository, BCryptPasswordEncoder passwordEncoder, SMSNotifServiceImpl smsNotifService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsNotifService = smsNotifService;
    }

    @Transactional
    public String withdraw(String phoneNumber, BigDecimal amount, String pin) throws Exception {
        Account account = accountRepository.findById(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if(passwordEncoder.matches(pin,account.getHashedPin())) {

            if (account.getBalance().compareTo(amount) < 0) {
                String message = "END Insufficient funds. Current Funds: " + account.getBalance();
                smsNotifService.sendSMS(phoneNumber,message);
                return message;
            }
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);
            String message = "END Withdrawal successful. New balance: " + account.getBalance();
            smsNotifService.sendSMS(phoneNumber,message);
            return message;
        }
        throw new InvalidPinException("invalid pin");
    }


    public String createAccount(String phoneNumber, String pin) throws Exception {
        Optional<Account> account = accountRepository.findById(phoneNumber);
        if(account.isPresent()){
            throw new RuntimeException("Account already exist for "+phoneNumber);
        }
        Account newAccount = new Account(phoneNumber,passwordEncoder.encode(pin),BigDecimal.ZERO);
        accountRepository.save(newAccount);
        String message = "END Account created successfully. Account Number: " + phoneNumber;
        smsNotifService.sendSMS(phoneNumber,message);
        return message;
    }

    @Transactional
    public String deposit(String phoneNumber, BigDecimal amount) throws Exception {
        Account account = accountRepository.findById(phoneNumber).orElseThrow(()->new RuntimeException("Account doesn't exist"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        String message = "END Account credited with: " + amount + "New Balance: " + account.getBalance();
        smsNotifService.sendSMS(phoneNumber,message);
        return message;
    }

    public BigDecimal checkBalance(String phoneNumber, String pin) throws Exception {
        Account account = accountRepository.findById(phoneNumber).orElseThrow(()-> new RuntimeException("Account doesn't exist"));
        if(passwordEncoder.matches(pin,account.getHashedPin())){
            BigDecimal balance = account.getBalance();
            smsNotifService.sendSMS(phoneNumber,String.valueOf(balance));
            return balance;
        }
        throw new InvalidPinException("Invalid Pin");
    }
}
