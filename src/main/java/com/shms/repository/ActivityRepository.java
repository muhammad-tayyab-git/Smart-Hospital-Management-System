package com.shms.repository;

import com.shms.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findTop10ByOrderByTimestampDesc();
}
