package com.bogolyandras.iotlogger.value.device;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewDevice {

    @NotNull
    @Size(min = 1, max = 60)
    private final String name;

    @Size(min = 1, max = 1024)
    private final String description;

    @JsonCreator
    public NewDevice(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
