package com.bogolyandras.iotlogger.value.initialize;

public class FirstUserCredentialsWithEncodedPassword {

    private final FirstUserCredentials firstUserCredentials;
    private final String passwordHash;

    public FirstUserCredentialsWithEncodedPassword(FirstUserCredentials firstUserCredentials, String passwordHash) {
        this.firstUserCredentials = firstUserCredentials;
        this.passwordHash = passwordHash;
    }

    public FirstUserCredentials getFirstUserCredentials() {
        return firstUserCredentials;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

}
