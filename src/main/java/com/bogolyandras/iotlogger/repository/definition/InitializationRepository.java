package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.value.initialize.FirstUserCredentialsWithEncodedPassword;
import com.bogolyandras.iotlogger.value.initialize.InitialCredentials;

public interface InitializationRepository {

    InitialCredentials getInitialCredentials(String passwordIfNotInitialized);
    String disableInitialCredentialsAndAddFirstUser(FirstUserCredentialsWithEncodedPassword firstUserCredentials);

}
