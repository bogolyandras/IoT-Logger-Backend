package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.dto.authentication.UsernamePassword;
import com.bogolyandras.iotlogger.entity.ApplicationUser;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import com.bogolyandras.iotlogger.security.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public JwtToken attemptLogin(UsernamePassword usernamePassword) {
        ApplicationUser account = userRepository.findAccountByUsername(usernamePassword.getUsername());
        if (account == null) {
            throw new UsernameNotFoundException("No user " + usernamePassword.getUsername() + " can be found.");
        }
        if (!account.getEnabled()) {
            throw new DisabledException("User " + usernamePassword.getUsername() + " has been disabled!");
        }
        if (!passwordEncoder.matches(usernamePassword.getPassword(), account.getPassword())) {
            throw new BadCredentialsException("Password is incorrect!");
        }
        return JwtToken.builder()
                .token(jwtService.issueToken(account.getId()))
                .build();
    }

    public JwtUser loadUserById(String identifier) {

        ApplicationUser applicationUser = userRepository.findAccountById(identifier);

        if (applicationUser == null) {
            throw new UsernameNotFoundException("No user with id of " + identifier + " found.");
        }
        if (!applicationUser.getEnabled()) {
            throw new DisabledException("Account has been disabled!");
        }

        List<String> authorities;
        switch (applicationUser.getUserType()) {
            case User:
                authorities = Collections.singletonList("ROLE_USER");
                break;
            case Administrator:
                authorities = Arrays.asList("ROLE_USER", "ROLE_ADMINISTRATOR");
                break;
            default:
                throw new RuntimeException("User type is not supported!");
        }
        return new JwtUser(
                applicationUser.getId(),
                applicationUser.getUsername(),
                applicationUser.getEnabled(),
                authorities
        );
    }

}
