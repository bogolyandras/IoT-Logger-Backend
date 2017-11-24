package com.bogolyandras.iotlogger.value.account;

public class NewAccountWithPasswordHash {

    private final NewAccount newAccount;
    private final String passwordHash;

    public NewAccountWithPasswordHash(NewAccount newAccount, String passwordHash) {
        this.newAccount = newAccount;
        this.passwordHash = passwordHash;
    }

    public NewAccount getNewAccount() {
        return newAccount;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

}
