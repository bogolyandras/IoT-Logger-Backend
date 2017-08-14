package com.bogolyandras.iotlogger.dto.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UsernamePassword {

    @NotNull
    @Size(min = 1, max = 60)
    private final String username;

    @NotNull
    @Size(min = 1, max = 60)
    private final String password;

    @JsonCreator
    public UsernamePassword(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
