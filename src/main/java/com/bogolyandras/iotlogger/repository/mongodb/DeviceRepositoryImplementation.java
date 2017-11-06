package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public class DeviceRepositoryImplementation implements DeviceRepository {
}
