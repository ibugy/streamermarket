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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ibugy.wokege.twitch.model.TwitchOauthToken;

@Service
public class TwitchOauthService {

	private static final Log LOG = LogFactory.getLog(TwitchOauthService.class);
	private RestTemplate restTemplate;
	private URI apiUrl;

	public TwitchOauthService(@Value("${twitch.oauth.url}") String apiUrlString) throws URISyntaxException {
		restTemplate = new RestTemplateBuilder().build();
		apiUrl = new URI(apiUrlString);
	}

	public TwitchOauthToken getAccessToken(String clientId, String clientSecret) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("client_id", clientId);
		form.add("client_secret", clientSecret);
		form.add("grant_type", "client_credentials");
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(form, headers);
		TwitchOauthToken token = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, TwitchOauthToken.class)
			.getBody();
		LOG.info("Received token from twitch oauth2: " + token);
		return token;
	}
}
