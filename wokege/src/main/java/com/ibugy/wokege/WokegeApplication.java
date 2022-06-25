package com.ibugy.wokege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ibugy.wokege.job.StreamDataCollector;

@SpringBootApplication
@EnableScheduling
public class WokegeApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(WokegeApplication.class, args);
		StreamDataCollector collectorThread = context.getBean(StreamDataCollector.class);
		collectorThread.run();
	}
}
