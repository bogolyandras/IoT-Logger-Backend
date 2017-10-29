package com.bogolyandras.iotlogger.value.initialize;

public class InitialCredentials {

    private final String password;

    private final Boolean initialized;

    public InitialCredentials(String password, Boolean initialized) {
        this.password = password;
        this.initialized = initialized;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getInitialized() {
        return initialized;
    }

}
