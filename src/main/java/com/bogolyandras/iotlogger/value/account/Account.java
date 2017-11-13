package com.bogolyandras.iotlogger.value.account;

import java.time.Instant;

public class Account {

    private final String id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final Instant registrationTime;

    public Account(String id, String username, String firstName, String lastName, Instant registrationTime) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationTime = registrationTime;
    }

    public String getId() {
        return id;
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

    public Instant getRegistrationTime() {
        return registrationTime;
    }

}
