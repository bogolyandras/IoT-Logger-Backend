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

    private String lastPrintedRandomPassword = null;
    private byte[] randomBytes = new byte[10];
    private Random random = new Random();

    public FirstUserService(InitializationRepository initializationRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.initializationRepository = initializationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;


    }

    @Scheduled(fixedRate = DATABASE_INITIALIZATION_RETRY_RATE)
    public void initializeFirstUserPasswordIfDoesNotExist() {

        random.nextBytes(randomBytes);
        String randomPassword = DatatypeConverter.printHexBinary(randomBytes);

        InitialCredentials initialCredentials = initializationRepository.getInitialCredentials(randomPassword);

        if (initialCredentials == null) {
            logger.info("Failed to initialize the database, will retry in " + (DATABASE_INITIALIZATION_RETRY_RATE / 1000) + " seconds");
            return;
        }

        if (!initialCredentials.getInitialized() && !initialCredentials.getPassword().equals(lastPrintedRandomPassword)) {
            logger.info("The first user has not been created. " +
                    "You can use the following password for this action");
            logger.info("******************************");
            logger.info("*****" + initialCredentials.getPassword() + "*****");
            logger.info("******************************");

            lastPrintedRandomPassword = initialCredentials.getPassword();
        }

    }

    public JwtToken initializeFirstUser(FirstUserCredentials firstUserCredentials) {

        random.nextBytes(randomBytes);
        String randomPassword = DatatypeConverter.printHexBinary(randomBytes);

        InitialCredentials initialCredentials = initializationRepository.getInitialCredentials(randomPassword);
        if (initialCredentials.getInitialized()) {
            throw new AccessDeniedException("Already initialized!");
        }
        if (!firstUserCredentials.getServerPassword().equals(initialCredentials.getPassword())) {
            throw new BadCredentialsException("Incorrect password!");
        }

        String userId = initializationRepository.disableInitialCredentialsAndAddFirstUser(
                new FirstUserCredentialsWithPasswordHash(firstUserCredentials, passwordEncoder.encode(firstUserCredentials.getPassword()))
        );

        return new JwtToken(
            jwtService.issueToken(userId)
        );
    }

    public boolean isFirstUserSet() {

        random.nextBytes(randomBytes);
        String randomPassword = DatatypeConverter.printHexBinary(randomBytes);

        InitialCredentials initialCredentials = initializationRepository.getInitialCredentials(randomPassword);

        if (initialCredentials == null) {
            return false;
        }

        return initialCredentials.getInitialized();

    }

}
