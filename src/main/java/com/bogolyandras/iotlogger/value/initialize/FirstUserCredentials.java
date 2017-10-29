package com.bogolyandras.iotlogger.value.initialize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FirstUserCredentials {

    @NotNull
    @Size(min = 20, max = 20)
    private final String serverPassword;

    @NotNull
    @Size(min = 1, max = 60)
    private final String username;

    @NotNull
    @Size(min = 1, max = 60)
    private final String password;

    @NotNull
    @Size(min = 1, max = 60)
    private final String firstName;

    @NotNull
    @Size(min = 1, max = 60)
    private final String lastName;

    @JsonCreator
    public FirstUserCredentials(
            @JsonProperty("serverPassword") String serverPassword,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName) {
        this.serverPassword = serverPassword;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
