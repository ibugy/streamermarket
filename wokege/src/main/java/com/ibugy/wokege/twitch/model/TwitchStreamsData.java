package com.ibugy.wokege.twitch.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchStreamsData {

	List<TwitchStreamInfo> data;
}
