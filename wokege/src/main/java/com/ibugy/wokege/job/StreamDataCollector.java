package com.ibugy.wokege.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ibugy.streamermarket.common.model.DailyStreamData;
import com.ibugy.streamermarket.common.model.Streamer;
import com.ibugy.streamermarket.common.repository.DailyStreamDataRepository;
import com.ibugy.streamermarket.common.repository.StreamerRepository;
import com.ibugy.wokege.twitch.business.TwitchBusiness;
import com.ibugy.wokege.twitch.model.TwitchStreamInfo;
import com.ibugy.wokege.twitch.model.TwitchStreamsData;
import com.ibugy.wokege.twitch.model.TwitchUsersData;

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

	@Scheduled(cron = "${wokege.job.streams.collector}")
	private void collectStreamsData() {
		LOG.info("**** STREAM DATA COLLECTOR STARTED ****");
		TwitchStreamsData data = twitchBusiness.getStreamsInfo(twitchStreamsCollectedData.keySet()
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

	@Scheduled(cron = "${wokege.job.streams.processor}")
	public void processStreamsData() {
		LOG.info("**** STREAM DATA PROCESSOR STARTED ****");
		ArrayList<String> streamersToUpdate = saveTodayStreamData();
		updateStreamerCoinValue(streamerRepository.findByNameIn(streamersToUpdate));
		LOG.info("**** STREAM DATA PROCESSOR FINISHED ****");
	}

	@Scheduled(cron = "${wokege.job.streamers.collector}")
	public void collectStreamerData() {
		LOG.info("**** STREAMER DATA COLLECTOR STARTED ****");
		TwitchUsersData data = twitchBusiness.getUsersInfo(twitchStreamsCollectedData.keySet()
			.toArray(String[]::new));
		ArrayList<Streamer> streamersToSave = new ArrayList<>();
		data.getData()
			.stream()
			.forEach(userInfo -> {
				streamersToSave.add(new Streamer(userInfo.getLogin(), null, userInfo.getProfileImageUrl(), userInfo.getDescription()));
			});
		streamerRepository.saveAll(streamersToSave);
	}

	private ArrayList<String> saveTodayStreamData() {
		ArrayList<DailyStreamData> streamsToSave = new ArrayList<>();
		ArrayList<String> streamersToUpdate = new ArrayList<>();
		Set<String> streamers = twitchStreamsCollectedData.keySet();
		streamers.stream()
			.forEach(streamer -> {
				ArrayList<TwitchStreamInfo> streams = twitchStreamsCollectedData.get(streamer);
				if (streams.size() > 0) {
					streamersToUpdate.add(streamer);
					int viewsSum = 0;
					int streamsCount = 0;
					int avgViews;
					for (TwitchStreamInfo streamInfo : streams) {
						viewsSum += streamInfo.getViewerCount();
						streamsCount++;
					}
					avgViews = viewsSum / streamsCount;
					LOG.info("Streamer " + streamer + " has been tracked " + streamsCount + " times");
					LOG.info("Streamer " + streamer + " had " + avgViews + " average views this period");
					streamsToSave.add(new DailyStreamData(new Date(), streamer, avgViews));
				}
				streams.clear();
			});
		streamRepository.saveAll(streamsToSave);
		return streamersToUpdate;
	}

	private void updateStreamerCoinValue(List<Streamer> streamersToUpdate) {
		// ! - Calculate average views last 7 days and adjust streamer coinValue
		streamersToUpdate.forEach(streamer -> {
			List<DailyStreamData> dailyStreamDataList = streamRepository.findTop7ByStreamerOrderByDateDesc(streamer.getName());
			BigDecimal coinValue = BigDecimal.valueOf(0);
			dailyStreamDataList.forEach(dsd -> {
				coinValue.add(BigDecimal.valueOf(dsd.getAvgViews()));
			});
			coinValue.divide(BigDecimal.valueOf(dailyStreamDataList.size()));
			LOG.info(
				"Saving new coinValue for " + streamer.getName() + ". Was: " + streamer.getCoinValue() + ". Now it is " + coinValue + ".");
			streamer.setCoinValue(coinValue);
			streamerRepository.save(streamer);
		});
		LOG.info("Finished updating streamers coin values");
	}
}
