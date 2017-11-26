package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.bogolyandras.iotlogger.utility.SecurityUtility;
import com.bogolyandras.iotlogger.value.account.Account;
import com.bogolyandras.iotlogger.value.account.ApplicationUser;
import com.bogolyandras.iotlogger.value.account.NewAccount;
import com.bogolyandras.iotlogger.value.account.NewAccountWithPasswordHash;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Account> getAccounts() {
        return userRepository.getAllUsers()
                .stream()
                .map(this::convertApplicationUserIntoAccount)
                .collect(Collectors.toList());
    }

    public Account addAccount(NewAccount newAccount) {
        return convertApplicationUserIntoAccount(userRepository.addAccount(
                new NewAccountWithPasswordHash(newAccount, (newAccount.getPassword() != null) ? passwordEncoder.encode(newAccount.getPassword()) : null)
            )
        );
    }

    public Account patchAccountById(String identifier, NewAccount newAccount) {
        return convertApplicationUserIntoAccount(userRepository.patchAccount(
                identifier,
                new NewAccountWithPasswordHash(newAccount, (newAccount.getPassword() != null) ? passwordEncoder.encode(newAccount.getPassword()) : null)
            )
        );
    }

    public void deleteAccount(String identifier) {

        if (SecurityUtility.getLoggedInUserId().equals(identifier)) {
            throw new AccessDeniedException("Can't delete yourself!");
        }

        userRepository.deleteAccount(identifier);

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
