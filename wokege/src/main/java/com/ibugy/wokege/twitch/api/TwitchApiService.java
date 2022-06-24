package com.ibugy.wokege.twitch.api;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ibugy.wokege.twitch.model.TwitchStreamInfo;
import com.ibugy.wokege.twitch.model.TwitchStreamsData;
import com.ibugy.wokege.twitch.model.TwitchUserInfo;
import com.ibugy.wokege.twitch.model.TwitchUsersData;

@Service
public class TwitchApiService {

	private static final Log LOG = LogFactory.getLog(TwitchApiService.class);
	private RestTemplate restTemplate;
	private URI usersUrl;
	private URI streamsUrl;

	public TwitchApiService(@Value("${twitch.api.url.users}") String usersUrlString, @Value("${twitch.api.url.streams}") String streamsUrlString) throws URISyntaxException {
		restTemplate = new RestTemplateBuilder().build();
		usersUrl = new URI(usersUrlString);
		streamsUrl = new URI(streamsUrlString);
	}

	public TwitchUserInfo getUser(String user, String oauthToken, String clientId) {
		HttpHeaders headers = createHeadersWithAuth(oauthToken, clientId);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String requestUrl = usersUrl.toString() + "?login=" + user;
		TwitchUsersData usersData = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, TwitchUsersData.class)
			.getBody();
		TwitchUserInfo userInfo = usersData.getData()
			.get(0);
		LOG.info("Received users info from twitch api: " + userInfo);
		return userInfo;
	}

	public TwitchStreamInfo getStream(String user, String oauthToken, String clientId) {
		HttpHeaders headers = createHeadersWithAuth(oauthToken, clientId);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);
		String requestUrl = streamsUrl.toString() + "?login=" + user;
		TwitchStreamsData streamsData = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, TwitchStreamsData.class)
			.getBody();
		TwitchStreamInfo userInfo = streamsData.getData()
			.get(0);
		LOG.info("Received stream info from twitch api: " + userInfo);
		return userInfo;
	}

	private HttpHeaders createHeadersWithAuth(String oauthToken, String clientId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer" + " " + oauthToken);
		headers.add("Client-Id", clientId);
		return headers;
	}
}
