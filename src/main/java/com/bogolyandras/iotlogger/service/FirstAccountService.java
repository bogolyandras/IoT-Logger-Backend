package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.dto.FirstUserCredentials;
import com.bogolyandras.iotlogger.entity.InitialCredentials;
import com.bogolyandras.iotlogger.repository.definition.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.util.Random;

@Service
public class FirstAccountService {

    private static final Logger logger = LoggerFactory.getLogger(FirstAccountService.class);

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private boolean firstUserSet = false;

    @Autowired
    public FirstAccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeFirstUserPasswordIfDoesNotExist() {

        InitialCredentials initialCredentials = userRepository.getInitialCredentials();

        if (initialCredentials == null) {
            byte[] randomBytes = new byte[10];
            new Random().nextBytes(randomBytes);
            String randomPassword = DatatypeConverter.printHexBinary(randomBytes);
            userRepository.addInitialCredentials(
                    InitialCredentials.builder()
                            .initialized(false)
                            .password(randomPassword)
                    .build()
            );
            logger.info("A new password has been generated to create the first user with");
            logPassword(randomPassword);
        } else if (!initialCredentials.getInitialized()) {
            logger.info("The first user has not been created. " +
                    "You can use the following password for this action");
            logPassword(initialCredentials.getPassword());
        } else {
            firstUserSet = true;
        }

    }

    public void initializeFirstUser(FirstUserCredentials firstUserCredentials) {
        InitialCredentials initialCredentials = userRepository.getInitialCredentials();
        if (initialCredentials.getInitialized()) {
            throw new AccessDeniedException("Already initialized!");
        }
        if (!firstUserCredentials.getServerPassword().equals(initialCredentials.getPassword())) {
            throw new AccessDeniedException("Incorrect password!");
        }
        firstUserCredentials.setPassword(passwordEncoder.encode(firstUserCredentials.getPassword()));
        userRepository.disableInitialCredentialsAndAddFirstUser(firstUserCredentials);
    }

    private void logPassword(String passwordToBeLogged) {
        logger.info("******************************");
        logger.info("*****" + passwordToBeLogged + "*****");
        logger.info("******************************");
    }

    public boolean isFirstUserSet() {
        return firstUserSet;
    }

}
