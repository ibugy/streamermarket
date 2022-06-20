package com.ibugy.wokege.twitch.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TwitchUserInfo {

	private Long id;
	private String broadcasterType;
	private String description;
	private String displayName;
	private String login;
	private String offlineImageUrl;
	private String profileImageUrl;
	private String type;
	private int viewCount;
	private String email;
	private Date createdAt;
}
