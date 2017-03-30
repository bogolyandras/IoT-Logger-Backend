package com.bogolyandras.iotlogger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class IoTLoggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IoTLoggerApplication.class, args);
	}
}
