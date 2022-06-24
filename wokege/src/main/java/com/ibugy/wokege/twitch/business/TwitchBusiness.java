package com.ibugy.wokege.twitch.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibugy.wokege.twitch.api.TwitchApiService;
import com.ibugy.wokege.twitch.api.TwitchOauthService;
import com.ibugy.wokege.twitch.model.TwitchOauthToken;
import com.ibugy.wokege.twitch.model.TwitchStreamsData;
import com.ibugy.wokege.twitch.model.TwitchUsersData;

@Service
public class TwitchBusiness {

	private static final Log LOG = LogFactory.getLog(TwitchBusiness.class);
	@Autowired
	private TwitchApiService twitchApiService;
	@Autowired
	private TwitchOauthService twitchOauthService;
	private TwitchOauthToken twitchApiToken;
	private String clientId;
	private String clientSecret;

	public TwitchBusiness(@Value("${twitch.oauth.secret.location}") String clientSecretLocation, @Value("${twitch.oauth.clientid.location}") String clientIdLocation) throws FileNotFoundException {
		LOG.info("Getting client credentials from files");
		LOG.info(clientIdLocation);
		LOG.info(clientSecretLocation);
		File clientIdFile = new File(clientIdLocation);
		try (Scanner clientIdReader = new Scanner(clientIdFile)) {
			clientId = clientIdReader.nextLine();
		}
		LOG.info("Initialized client-id as: " + clientId);
		File clientSecretFile = new File(clientSecretLocation);
		try (Scanner clientSecretReader = new Scanner(clientSecretFile)) {
			clientSecret = clientSecretReader.nextLine();
			LOG.info("Initialized client-secret as: ****" + clientSecret.substring(clientSecret.length() - 3, clientSecret.length() - 1));
		}
	}

	public TwitchUsersData getUserInfo(String... users) {
		renewToken();
		return twitchApiService.getUsers(twitchApiToken.getAccessToken(), clientId, users);
	}

	public TwitchStreamsData getStreamInfo(String... streamers) {
		renewToken();
		return twitchApiService.getStreams(twitchApiToken.getAccessToken(), clientId, streamers);
	}

	private void renewToken() {
		// TODO include if token.isExpired
		if (twitchApiToken == null) {
			LOG.info("Requesting twitch api oauth token...");
			twitchApiToken = twitchOauthService.getAccessToken(clientId, clientSecret);
			LOG.info(twitchApiToken);
		}
	}
}
