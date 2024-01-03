package com.example.A201.battery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BatteryDTO {
    private Long memberId;
    private String modelName;
    private String code;
    private String batteryStatus;
}
