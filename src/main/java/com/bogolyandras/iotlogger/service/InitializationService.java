package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.domain.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.dto.authentication.JwtToken;
import com.bogolyandras.iotlogger.dto.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.InitializationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.Random;

@Service
public class InitializationService {

    private static final Logger logger = LoggerFactory.getLogger(InitializationService.class);

    private final InitializationRepository initializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private boolean databaseInitialized = false;
    private boolean firstUserSet = false;
    private String randomPassword;

    public InitializationService(InitializationRepository initializationRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.initializationRepository = initializationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

        byte[] randomBytes = new byte[10];
        new Random().nextBytes(randomBytes);
        randomPassword = DatatypeConverter.printHexBinary(randomBytes);
    }

    @Scheduled(fixedRate = 1000 * 10)
    public void initializeFirstUserPasswordIfDoesNotExist() {

        if (databaseInitialized) {
            return;
        }

        InitialCredentials initialCredentials = initializationRepository.getInitialCredentials(randomPassword);

        if (initialCredentials == null) {
            logger.info("Failed to initialize the database");
        } else if (!initialCredentials.getInitialized()) {
            logger.info("The first user has not been created. " +
                    "You can use the following password for this action");
            logPassword(initialCredentials.getPassword());
        } else {
            firstUserSet = true;
        }

    }

    public JwtToken initializeFirstUser(FirstUserCredentials firstUserCredentials) {
        InitialCredentials initialCredentials = initializationRepository.getInitialCredentials(randomPassword);
        if (initialCredentials.getInitialized()) {
            throw new AccessDeniedException("Already initialized!");
        }
        if (!firstUserCredentials.getServerPassword().equals(initialCredentials.getPassword())) {
            throw new AccessDeniedException("Incorrect password!");
        }
        firstUserCredentials.setPassword(passwordEncoder.encode(firstUserCredentials.getPassword()));
        firstUserSet = true;
        return new JwtToken(jwtService.issueToken(initializationRepository.disableInitialCredentialsAndAddFirstUser(firstUserCredentials)));
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
