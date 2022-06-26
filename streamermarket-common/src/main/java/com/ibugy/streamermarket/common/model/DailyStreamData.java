package com.ibugy.streamermarket.common.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(DailyStreamDataId.class)
public class DailyStreamData {

	@Id
	private Date date;
	@Id
	private String streamer;
	private int avgViews;
}
