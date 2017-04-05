package com.bogolyandras.iotlogger.repository.definition;

import com.bogolyandras.iotlogger.entity.InitialCredentials;

public interface FirstUserRepository {

    InitialCredentials getInitialCredentials();
    void addInitialCredentials(InitialCredentials initialCredentials);
    void updateInitialCredentials(InitialCredentials initialCredentials);

}
