package com.bogolyandras.iotlogger.configuration;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("sql")
@Import(LiquibaseAutoConfiguration.class)
public class SqlConfiguration {
}
