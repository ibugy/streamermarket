package com.ibugy.wokege.twitch.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TwitchStreamInfo {

	private String id;
	private String userId;
	private String userLogin;
	private String userName;
	private String gameId;
	private String gameName;
	private String type;
	private String title;
	private int viewerCount;
	private Date startedAt;
	private String language;
	private String thumbnailUrl;
	private String tagIds;
	private boolean isMature;
}