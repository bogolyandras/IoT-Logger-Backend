package com.bogolyandras.iotlogger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import java.util.TimeZone;

@SpringBootApplication(exclude = {
		MongoAutoConfiguration.class
	})
public class IoTLoggerApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(IoTLoggerApplication.class, args);
	}

}
