package com.ibugy.streamermarket.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ibugy.streamermarket.common.model.DailyStreamData;

@Repository
public interface DailyStreamDataRepository extends JpaRepository<DailyStreamData, Long> {
}
