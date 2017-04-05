package com.bogolyandras.iotlogger.configuration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
@ImportAutoConfiguration(DataSourceAutoConfiguration.class)
public class SqlConfiguration {
}
