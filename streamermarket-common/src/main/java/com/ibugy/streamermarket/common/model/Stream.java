package com.ibugy.streamermarket.common.model;

import java.util.Date;

import lombok.Data;

@Data
public class Stream {

	private Long id;
	private Date date;
	private String streamer;
	private int avgViews;
}
