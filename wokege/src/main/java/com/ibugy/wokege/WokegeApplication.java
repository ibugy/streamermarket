package com.ibugy.wokege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.ibugy.streamermarket.common.repository")
@EntityScan("com.ibugy.streamermarket.common.model")
public class WokegeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WokegeApplication.class, args);
	}
}
