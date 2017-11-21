package com.bogolyandras.iotlogger.service;

import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentialsWithPasswordHash;
import com.bogolyandras.iotlogger.value.initialize.InitialCredentials;
import com.bogolyandras.iotlogger.value.authentication.JwtToken;
import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentials;
import com.bogolyandras.iotlogger.repository.definition.InitializationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.Random;

@Service
public class FirstUserService {

    private static final Logger logger = LoggerFactory.getLogger(FirstUserService.class);
    private static final long DATABASE_INITIALIZATION_RETRY_RATE = 1000 * 10;

    private final InitializationRepository initializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private boolean databaseInitialized = false;
    private boolean firstUserSet = false;
    private String randomPassword;

    public FirstUserService(InitializationRepository initializationRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.initializationRepository = initializationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

        byte[] randomBytes = new byte[10];
        new Random().nextBytes(randomBytes);
        randomPassword = DatatypeConverter.printHexBinary(randomBytes);
    }

    @Scheduled(fixedRate = DATABASE_INITIALIZATION_RETRY_RATE)
    public void initializeFirstUserPasswordIfDoesNotExist() {

        if (databaseInitialized) {
            return;
        }

        InitialCredentials initialCredentials = initializationRepository.getInitialCredentials(randomPassword);

        if (initialCredentials == null) {
            logger.info("Failed to initialize the database, will retry in " + (DATABASE_INITIALIZATION_RETRY_RATE / 1000) + " seconds");
            return;
        }

        databaseInitialized = true;

        if (!initialCredentials.getInitialized()) {
            logger.info("The first user has not been created. " +
                    "You can use the following password for this action");
            logger.info("******************************");
            logger.info("*****" + initialCredentials.getPassword() + "*****");
            logger.info("******************************");
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
            throw new BadCredentialsException("Incorrect password!");
        }
        firstUserSet = true;
        return new JwtToken(
            jwtService.issueToken(
                initializationRepository.disableInitialCredentialsAndAddFirstUser(
                    new FirstUserCredentialsWithPasswordHash(firstUserCredentials, passwordEncoder.encode(firstUserCredentials.getPassword()))
                )
            )
        );
    }

    public boolean isFirstUserSet() {
        return firstUserSet;
    }

}
