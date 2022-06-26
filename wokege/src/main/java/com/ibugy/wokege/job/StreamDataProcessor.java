package com.ibugy.wokege.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ibugy.streamermarket.common.model.DailyStreamData;
import com.ibugy.streamermarket.common.repository.DailyStreamDataRepository;
import com.ibugy.streamermarket.common.repository.StreamerRepository;
import com.ibugy.wokege.twitch.model.TwitchStreamInfo;

@Component
public class StreamDataProcessor {

	private static final Log LOG = LogFactory.getLog(StreamDataProcessor.class);
	@Autowired
	private ApplicationContext context;
	@Autowired
	private DailyStreamDataRepository streamRepository;
	@Autowired
	private StreamerRepository streamerRepository;

	@Scheduled(cron = "${wokege.job.stream.data.processor}")
	public void processStreamData() {
		LOG.info("**** STREAM DATA PROCESSOR JOB STARTING ****");
		StreamDataCollector dataCollector = context.getBean(StreamDataCollector.class);
		dataCollector.stop();
		HashMap<String, ArrayList<TwitchStreamInfo>> collectedData = dataCollector.getCollectedData();
		ArrayList<DailyStreamData> streamsToSave = new ArrayList<>();
		Set<String> streamers = collectedData.keySet();
		streamers.stream()
			.forEach(streamer -> {
				ArrayList<TwitchStreamInfo> streams = collectedData.get(streamer);
				if (streams.size() > 0) {
					int viewsSum = 0;
					int streamsCount = 0;
					int avgViews;
					for (TwitchStreamInfo streamInfo : streams) {
						viewsSum += streamInfo.getViewerCount();
						streamsCount++;
					}
					avgViews = viewsSum / streamsCount;
					streamsToSave.add(new DailyStreamData(new Date(), streamer, avgViews));
				}
			});
		// ! - Calculate average views last 7 days and adjust streamer coinValue
		// TODO
		streamRepository.saveAll(streamsToSave);
		LOG.info("**** STREAM DATA PROCESSOR JOB FINISHED ****");
		dataCollector.run();
	}
}
