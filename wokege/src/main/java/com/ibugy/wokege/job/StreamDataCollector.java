package com.ibugy.wokege.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ibugy.streamermarket.common.model.DailyStreamData;
import com.ibugy.streamermarket.common.repository.DailyStreamDataRepository;
import com.ibugy.streamermarket.common.repository.StreamerRepository;
import com.ibugy.wokege.twitch.business.TwitchBusiness;
import com.ibugy.wokege.twitch.model.TwitchStreamInfo;
import com.ibugy.wokege.twitch.model.TwitchStreamsData;

@Component
public class StreamDataCollector {

	private static final Log LOG = LogFactory.getLog(StreamDataCollector.class);
	@Autowired
	private TwitchBusiness twitchBusiness;
	@Autowired
	private StreamerRepository streamerRepository;
	@Autowired
	private DailyStreamDataRepository streamRepository;
	private HashMap<String, ArrayList<TwitchStreamInfo>> twitchStreamsCollectedData;
	private static final long MIN_LIVE_TIME = 1000 * 60 * 10; // 10 minutes

	@PostConstruct
	public void postConstruct() {
		twitchStreamsCollectedData = new HashMap<>();
		streamerRepository.findAll()
			.stream()
			.forEach(streamer -> {
				twitchStreamsCollectedData.put(streamer.getName(), new ArrayList<>());
			});
	}

	@Scheduled(cron = "${wokege.job.stream.data.collector}")
	private void collectData() {
		LOG.info("**** STREAM DATA COLLECTOR STARTED ****");
		TwitchStreamsData data = twitchBusiness.getStreamInfo(twitchStreamsCollectedData.keySet()
			.toArray(String[]::new));
		data.getData()
			.stream()
			.forEach(streamInfo -> {
				String streamer = streamInfo.getUserLogin();
				// !- Will only add the stream if it's been live for more than MIN_LIVE_TIME
				long startedAt = streamInfo.getStartedAt()
					.getTime();
				long now = new Date().getTime();
				if ((now - startedAt) > MIN_LIVE_TIME)
					twitchStreamsCollectedData.get(streamer)
						.add(streamInfo);
			});
		LOG.info("**** STREAM DATA COLLECTOR FINISHED ****");
	}

	@Scheduled(cron = "${wokege.job.stream.data.processor}")
	public void processData() {
		LOG.info("**** STREAM DATA PROCESSOR STARTED ****");
		ArrayList<DailyStreamData> streamsToSave = new ArrayList<>();
		Set<String> streamers = twitchStreamsCollectedData.keySet();
		streamers.stream()
			.forEach(streamer -> {
				ArrayList<TwitchStreamInfo> streams = twitchStreamsCollectedData.get(streamer);
				if (streams.size() > 0) {
					int viewsSum = 0;
					int streamsCount = 0;
					int avgViews;
					for (TwitchStreamInfo streamInfo : streams) {
						viewsSum += streamInfo.getViewerCount();
						streamsCount++;
					}
					avgViews = viewsSum / streamsCount;
					LOG.info("Streamer " + streamer + " had " + avgViews + "average views today");
					streamsToSave.add(new DailyStreamData(new Date(), streamer, avgViews));
				}
			});
		// ! - Calculate average views last 7 days and adjust streamer coinValue
		// TODO
		streamRepository.saveAll(streamsToSave);
		LOG.info("**** STREAM DATA PROCESSOR FINISHED ****");
	}
}
