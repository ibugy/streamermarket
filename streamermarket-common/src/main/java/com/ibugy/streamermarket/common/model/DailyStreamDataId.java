package com.ibugy.streamermarket.common.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailyStreamDataId implements Serializable {

	private static final long serialVersionUID = -1044006209235735087L;
	private Date date;
	private String streamer;
}
