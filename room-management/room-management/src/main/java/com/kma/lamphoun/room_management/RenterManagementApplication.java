package com.kma.lamphoun.room_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RenterManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenterManagementApplication.class, args);
	}

}
