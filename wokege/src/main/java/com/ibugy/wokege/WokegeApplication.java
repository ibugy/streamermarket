package com.ibugy.wokege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ibugy.wokege.business.StreamDataProcessor;

@SpringBootApplication
public class WokegeApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(WokegeApplication.class, args);
		StreamDataProcessor thread = context.getBean(StreamDataProcessor.class);
		thread.run();
	}
}
