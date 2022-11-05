package com.ibugy.wokege.twitch.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ibugy.wokege.twitch.model.TwitchStreamsData;
import com.ibugy.wokege.twitch.model.TwitchUsersData;

@Service
public class TwitchApiService {

	private static final Log LOG = LogFactory.getLog(TwitchApiService.class);
	private RestTemplate restTemplate;
	private URI usersUrl;
	private URI streamsUrl;

	public TwitchApiService(@Value("${twitch.api.url.users}") String usersUrlString,
		@Value("${twitch.api.url.streams}") String streamsUrlString) throws URISyntaxException {
		restTemplate = new RestTemplateBuilder().build();
		usersUrl = new URI(usersUrlString);
		streamsUrl = new URI(streamsUrlString);
	}

	public TwitchUsersData getUsers(String oauthToken, String clientId, String... users) {
		HttpHeaders headers = createHeadersWithAuth(oauthToken, clientId);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(usersUrl.toString());
		strBuilder.append("?login=" + users[0]);
		Arrays.asList(users)
			.stream()
			.forEach(user -> strBuilder.append("&login=" + user));
		String requestUrl = strBuilder.toString();
		TwitchUsersData usersData = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, TwitchUsersData.class)
			.getBody();
		LOG.info("Received users info from twitch api: " + usersData);
		return usersData;
	}

	public TwitchStreamsData getStreams(String oauthToken, String clientId, String... streamers) {
		HttpHeaders headers = createHeadersWithAuth(oauthToken, clientId);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(streamsUrl.toString());
		strBuilder.append("?user_login=" + streamers[0]);
		Arrays.asList(streamers)
			.stream()
			.skip(1)
			.forEach(user -> strBuilder.append("&user_login=" + user));
		String requestUrl = strBuilder.toString();
		TwitchStreamsData streamsData = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, TwitchStreamsData.class)
			.getBody();
		LOG.info("Received stream info from twitch api: " + streamsData);
		return streamsData;
	}

	private HttpHeaders createHeadersWithAuth(String oauthToken, String clientId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer" + " " + oauthToken);
		headers.add("Client-Id", clientId);
		return headers;
	}
}
