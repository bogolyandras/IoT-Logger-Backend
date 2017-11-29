package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import com.bogolyandras.iotlogger.value.device.Device;
import com.bogolyandras.iotlogger.value.device.NewDevice;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@Repository
@Profile("mongodb")
public class DeviceRepositoryImplementation implements DeviceRepository {

    private final MongoCollection<Document> devices;

    public DeviceRepositoryImplementation(MongoDatabase database) {
        this.devices = database.getCollection("devices");
    }

    @Override
    public List<Device> getDevicesForUser(String identifier) {
        FindIterable<Document> documents = this.devices.find(
                eq("ownerId", identifier)
        );
        MongoCursor<Device> iterator = documents.map(this::documentToDevice).iterator();
        List<Device> devices = new ArrayList<>();
        while(iterator.hasNext()) {
            devices.add(iterator.next());
        }
        return devices;
    }

    @Override
    public List<Device> getAllDevices() {
        FindIterable<Document> documents = this.devices.find();
        MongoCursor<Device> iterator = documents.map(this::documentToDevice).iterator();
        List<Device> devices = new ArrayList<>();
        while(iterator.hasNext()) {
            devices.add(iterator.next());
        }
        return devices;
    }

    @Override
    public Device getDevice(String identifier) {
        Document document = this.devices.find(eq("_id", new ObjectId(identifier))).first();
        if (document == null) {
            return null;
        } else {
            return documentToDevice(document);
        }
    }

    @Override
    public Device addDevice(NewDevice newDevice, String ownerId) {

        Document document = new Document("ownerId", ownerId)
                .append("name", newDevice.getName())
                .append("description", newDevice.getDescription());

        devices.insertOne(document);

        return documentToDevice(document);
    }

    @Override
    public void deleteDevice(String identifier) {

        DeleteResult deleteResult = devices.deleteOne(
            eq("_id", new ObjectId(identifier))
        );

        if (deleteResult.getDeletedCount() != 1) {
            throw new RuntimeException("Can't delete device " + identifier);
        }

    }

    @Override
    public void deleteDeviceWithOwnerOf(String identifier, String ownerId) {

        DeleteResult deleteResult = devices.deleteOne(
                and(
                        eq("_id", new ObjectId(identifier)),
                        eq("ownerId", ownerId)
                )
        );

        if (deleteResult.getDeletedCount() != 1) {
            throw new RuntimeException("Can't delete device " + identifier);
        }

    }

    @Override
    public Device patchDevice(String identifier, NewDevice newDevice) {

        UpdateResult updateResult = devices.updateOne(
                eq("_id", new ObjectId(identifier)),
                combine(
                        set("name", newDevice.getName()),
                        set("description", newDevice.getDescription())
                )
        );

        if (updateResult.getModifiedCount() != 1) {
            throw new RuntimeException("Modification failed for device " + identifier);
        }

        return getDevice(identifier);

    }

    @Override
    public Device patchDeviceWithOwnerOf(String identifier, NewDevice newDevice, String ownerId) {

        UpdateResult updateResult = devices.updateOne(
                and(
                        eq("_id", new ObjectId(identifier)),
                        eq("ownerId", ownerId)
                ),
                combine(
                        set("name", newDevice.getName()),
                        set("description", newDevice.getDescription())
                )
        );

        if (updateResult.getModifiedCount() != 1) {
            throw new RuntimeException("Modification failed for device " + identifier);
        }

        return getDevice(identifier);

    }

    private Device documentToDevice(Document document) {
        return new Device(
                document.getObjectId("_id").toString(),
                document.getString("ownerId"),
                document.getString("name"),
                document.getString("description")
        );
    }
}
