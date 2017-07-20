package com.bogolyandras.iotlogger.domain;

public class InitialCredentials {

    private String password;

    private Boolean initialized;

    public InitialCredentials(String password, Boolean initialized) {
        this.password = password;
        this.initialized = initialized;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }

}
