package com.bogolyandras.iotlogger.controller;

import com.bogolyandras.iotlogger.service.AccountService;
import com.bogolyandras.iotlogger.value.account.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/my")
    public Account getMyAccount() {
        throw new RuntimeException("Not implemented yet!");
    }

    @GetMapping("/{userId}")
    public Account getOtherAccount(@PathVariable("userId") String userId) {
        return accountService.getAccountById(userId);
    }

}
