package com.ibugy.streamermarket.common.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Streamer {

	@Id
	private String name;
	private BigDecimal coinValue;
	private String pfpUrl;
	private String description;
}
