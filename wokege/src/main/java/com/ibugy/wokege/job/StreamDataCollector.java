package com.ibugy.wokege.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ibugy.wokege.twitch.business.TwitchBusiness;
import com.ibugy.wokege.twitch.model.TwitchStreamInfo;
import com.ibugy.wokege.twitch.model.TwitchStreamsData;

/**
 * Runs a job that collects data from the twitch API for specific streamers listed in a file. The data is stored in a map with the name of
 * the streamer as a key, and a list of stream infos as value. This data can be retrieved with the method {@link #getCollectedData()}
 */
@Service
@Scope("singleton")
public class StreamDataCollector implements Runnable {

	private static final Log LOG = LogFactory.getLog(StreamDataCollector.class);
	@Autowired
	private TwitchBusiness twitchBusiness;
	private File streamersFile;
	private HashMap<String, ArrayList<TwitchStreamInfo>> twitchStreamsCollectedData;
	private boolean running;
	private static final long FREQUENCY = 10 * 1000; // 10 seconds
	private static final long MIN_LIVE_TIME = 1000 * 60 * 10; // 10 minutes

	public StreamDataCollector(@Value("${wokege.target.streamers.location}") String streamersFilePath) {
		streamersFile = new File(streamersFilePath);
	}

	@Override
	public void run() {
		running = true;
		LOG.info("**** STREAM DATA COLLECTOR STARTED ****");
		twitchStreamsCollectedData = new HashMap<>();
		try (Scanner scanner = new Scanner(streamersFile);) {
			LOG.info("Collecting stream data from: ");
			while (scanner.hasNextLine()) {
				String streamer = scanner.nextLine();
				twitchStreamsCollectedData.put(streamer, new ArrayList<>());
				LOG.info(streamer);
			}
		} catch (FileNotFoundException e) {
			LOG.error("Error reading target streamers file. Thread will exit.");
			LOG.error(e.getMessage(), e);
			return;
		}
		while (running) {
			long timeBeforeTask = new Date().getTime();
			long timeNextIteration = timeBeforeTask + FREQUENCY;
			collectData();
			long timeAfterTask = new Date().getTime();
			long timeToSleep = timeNextIteration - timeAfterTask;
			if (timeToSleep < 0) {
				LOG.warn("WARNING: Collect task is taking more time than the set frequency");
			} else {
				try {
					Thread.sleep(timeToSleep);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Returns the collected data. It is advised to use {@link #stop()} before collecting and processing the data, to avoid access
	 * conflicts. The process can be restarted after, using {@link #run()}
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<TwitchStreamInfo>> getCollectedData() {
		return twitchStreamsCollectedData;
	}

	/**
	 * Stops the process. It can be restarted after, using {@link #run()}
	 */
	public void stop() {
		running = false;
	}

	private void collectData() {
		TwitchStreamsData data = twitchBusiness.getStreamInfo(twitchStreamsCollectedData.keySet()
			.toArray(String[]::new));
		List<TwitchStreamInfo> streamsInfo = data.getData();
		streamsInfo.stream()
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
	}
}
