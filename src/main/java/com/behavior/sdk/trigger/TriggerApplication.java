package com.behavior.sdk.trigger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TriggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriggerApplication.class, args);
	}

}
