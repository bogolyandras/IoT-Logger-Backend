package com.bogolyandras.iotlogger.value.device;

public class Device {

    private final String id;
    private final String ownerId;
    private final String name;
    private final String description;

    public Device(String id, String ownerId, String name, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
