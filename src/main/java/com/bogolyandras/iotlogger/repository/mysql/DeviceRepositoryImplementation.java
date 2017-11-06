package com.bogolyandras.iotlogger.repository.mysql;

import com.bogolyandras.iotlogger.repository.definition.DeviceRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mysql")
public class DeviceRepositoryImplementation implements DeviceRepository {
}
