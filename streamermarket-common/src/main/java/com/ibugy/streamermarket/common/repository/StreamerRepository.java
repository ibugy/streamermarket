package com.ibugy.streamermarket.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ibugy.streamermarket.common.model.Streamer;

@Repository
public interface StreamerRepository extends JpaRepository<Streamer, String> {

	public List<Streamer> findByNameIn(List<String> names);
}
