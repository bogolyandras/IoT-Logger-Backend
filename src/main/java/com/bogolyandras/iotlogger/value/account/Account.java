package com.bogolyandras.iotlogger.value.account;

public class Account {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final Long registrationTime;

    public Account(String username, String firstName, String lastName, Long registrationTime) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationTime = registrationTime;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getRegistrationTime() {
        return registrationTime;
    }

}
