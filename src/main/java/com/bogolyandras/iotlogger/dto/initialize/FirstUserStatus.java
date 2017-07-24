package com.bogolyandras.iotlogger.dto.initialize;

public class FirstUserStatus {

    private Boolean initialized;

    public FirstUserStatus(Boolean initialized) {
        this.initialized = initialized;
    }

    public Boolean getInitialized() {
        return initialized;
    }

}
