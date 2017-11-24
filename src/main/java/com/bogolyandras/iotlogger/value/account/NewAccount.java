package com.bogolyandras.iotlogger.value.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewAccount {

    @NotNull(groups = {AddGroup.class, PatchGroup.class})
    @Size(min = 1, max = 60, groups = {AddGroup.class, PatchGroup.class})
    private final String username;

    @NotNull(groups = {AddGroup.class})
    @Size(min = 1, max = 60, groups = {AddGroup.class, PatchGroup.class})
    private final String password;

    @NotNull(groups = {AddGroup.class, PatchGroup.class})
    @Size(min = 1, max = 60, groups = {AddGroup.class, PatchGroup.class})
    private final String firstName;

    @NotNull(groups = {AddGroup.class, PatchGroup.class})
    @Size(min = 1, max = 60, groups = {AddGroup.class, PatchGroup.class})
    private final String lastName;

    @NotNull(groups = {AddGroup.class, PatchGroup.class})
    private final ApplicationUser.UserType userType;

    @JsonCreator
    public NewAccount(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("userType") ApplicationUser.UserType userType) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
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

    public ApplicationUser.UserType getUserType() {
        return userType;
    }

    public interface AddGroup { }
    public interface PatchGroup { }

}
