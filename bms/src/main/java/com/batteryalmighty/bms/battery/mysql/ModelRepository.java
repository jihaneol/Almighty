package com.batteryalmighty.bms.battery.mysql;

import com.batteryalmighty.bms.battery.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
