package com.ibugy.wokege.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ibugy.wokege.twitch.business.TwitchBusiness;
import com.ibugy.wokege.twitch.model.TwitchStreamsData;

@Service
@Scope("singleton")
public class StreamDataCollector implements Runnable {

	private static final Log LOG = LogFactory.getLog(StreamDataCollector.class);
	@Autowired
	private TwitchBusiness twitchBusiness;
	private File streamersFile;
	private List<TwitchStreamsData> twitchStreamsCollectedData;
	private boolean running;
	private static final long FREQUENCY = 10 * 1000;

	public StreamDataCollector(@Value("${wokege.target.streamers.location}") String streamersFilePath) {
		streamersFile = new File(streamersFilePath);
	}

	@Override
	public void run() {
		running = true;
		twitchStreamsCollectedData = new ArrayList<>();
		Scanner scanner = null;
//		TODO: Convert to try with resources
		try {
			scanner = new Scanner(streamersFile);
		} catch (FileNotFoundException e) {
			LOG.error("Error reading target streamers file. Thread will exit.");
			LOG.error(e.getMessage(), e);
			return;
		}
		ArrayList<String> targetStreams = new ArrayList<>();
		LOG.info("Stream data collector started. Collecting stream data from: ");
		while (scanner.hasNextLine()) {
			String stream = scanner.nextLine();
			targetStreams.add(stream);
			LOG.info(stream);
		}
		while (running) {
			twitchStreamsCollectedData.add(twitchBusiness.getStreamInfo(targetStreams.toArray(String[]::new)));
			try {
				Thread.sleep(FREQUENCY);
			} catch (InterruptedException e) {
				LOG.error(e.getStackTrace());
			}
		}
	}

	/**
	 * Returns a copy of the collected data
	 * 
	 * @return
	 */
	public List<TwitchStreamsData> getCollectedData() {
		return twitchStreamsCollectedData.stream()
			.collect(Collectors.toList());
	}

	public void stop() {
		running = false;
	}
}
