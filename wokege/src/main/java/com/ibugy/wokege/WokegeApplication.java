package com.ibugy.wokege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WokegeApplication {
//	TODO
//	private static Log LOG = LogFactory.getLog(WokegeApplication.class);

	public static void main(String[] args) {
		/* ConfigurableApplicationContext context = */ SpringApplication.run(WokegeApplication.class, args);
//		TODO Move this to the batch
//		StreamDataCollector collectorThread = context.getBean(StreamDataCollector.class);
//		collectorThread.run();
//		try {
//			Thread.sleep(11000);
//		} catch (InterruptedException e) {
//			LOG.error(e.getStackTrace());
//		}
//		LOG.info("**********HELOU?*************");
//		collectorThread.getCollectedData()
//			.stream()
//			.forEach(e -> LOG.info(e.getData()));
	}
}
