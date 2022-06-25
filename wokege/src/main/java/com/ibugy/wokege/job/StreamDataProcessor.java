package com.ibugy.wokege.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

import com.ibugy.wokege.twitch.model.TwitchStreamsData;

public class StreamDataProcessor {

	@Autowired
	private ApplicationContext context;

	@Scheduled(cron = "${wokege.job.stream.data.processor}")
	public void processStreamData() {
		StreamDataCollector dataCollector = context.getBean(StreamDataCollector.class);
		dataCollector.stop();
		List<TwitchStreamsData> collectedData = dataCollector.getCollectedData();
//		TODO Process data
		dataCollector.run();
	}
}
