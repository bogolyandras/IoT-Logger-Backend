package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.bogolyandras.iotlogger.value.account.Account;
import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final UserRepository userRepository;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Account> getAccounts() {
        return userRepository.getAllUsers()
                .stream()
                .map(this::convertApplicationUserIntoAccount)
                .collect(Collectors.toList());
    }

    public Account getAccountById(String identifier) {
        ApplicationUser applicationUser = userRepository.findAccountById(identifier);
        if (applicationUser != null) {
            return convertApplicationUserIntoAccount(applicationUser);
        } else {
            throw new NoSuchElementException("User " + identifier + " is not available.");
        }
    }

    public Account getAccountByUsername(String username) {
        ApplicationUser applicationUser = userRepository.findAccountByUsername(username);
        if (applicationUser != null) {
            return convertApplicationUserIntoAccount(applicationUser);
        } else {
            throw new NoSuchElementException("User " + username + " is not available.");
        }
    }

    private Account convertApplicationUserIntoAccount(ApplicationUser applicationUser) {
        return new Account(
                applicationUser.getId(),
                applicationUser.getUsername(),
                applicationUser.getFirstName(),
                applicationUser.getLastName(),
                applicationUser.getUserType(),
                applicationUser.getRegistrationTime()
        );
    }

}
