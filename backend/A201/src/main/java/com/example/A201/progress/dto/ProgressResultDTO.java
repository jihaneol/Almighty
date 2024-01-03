package com.example.A201.progress.dto;

import com.example.A201.history.constant.ResultStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class ProgressResultDTO {
    private Long progressId;
    private Long batteryId;
    private ResultStatus resultStatus;
    private String responseReason;
    private String requestReason;
}
