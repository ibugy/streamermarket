package com.ibugy.streamermarket.common.model;

import java.math.BigDecimal;

import javax.persistence.Entity;

import lombok.Data;

@Data
@Entity
public class Streamer {

	private String name;
	private BigDecimal CoinValue;
	private String pfpUrl;
	private String description;
}
