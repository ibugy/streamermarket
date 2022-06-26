package com.ibugy.wokege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ibugy.wokege.job.StreamDataCollector;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.ibugy.streamermarket.common.repository")
@EntityScan("com.ibugy.streamermarket.common.model")
public class WokegeApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(WokegeApplication.class, args);
		StreamDataCollector collectorThread = context.getBean(StreamDataCollector.class);
		collectorThread.run();
	}
}
