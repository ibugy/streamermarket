package com.ibugy.wokege.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibugy.wokege.twitch.business.TwitchBusiness;

@Service
public class StreamDataProcessor implements Runnable {

	private static final Log LOG = LogFactory.getLog(StreamDataProcessor.class);
//	TODO
//	@Autowired
//	private StreamerRepository streamerRepository;
	@Autowired
	private TwitchBusiness twitchBusiness;
	private File streamersFile;

	public StreamDataProcessor(@Value("${wokege.target.streamers.location}") String streamersFilePath) {
		streamersFile = new File(streamersFilePath);
	}

	@Override
	public void run() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(streamersFile);
		} catch (FileNotFoundException e) {
			LOG.error("Error reading target streamers file");
			LOG.error(e.getStackTrace());
		}
		ArrayList<String> targetStreams = new ArrayList<>();
//		TODO
//		ArrayList<TwitchStreamInfo> streamInfoList = new ArrayList<>();
		while (scanner.hasNextLine()) {
			String stream = scanner.nextLine();
			targetStreams.add(stream);
		}
//		TODO
//		targetStreams.stream()
//			.forEach(name -> streamInfoList.add(twitchBusiness.getStreamInfo(name)));
		targetStreams.stream()
			.forEach(name -> LOG.info(twitchBusiness.getStreamInfo(name)));
	}
}
