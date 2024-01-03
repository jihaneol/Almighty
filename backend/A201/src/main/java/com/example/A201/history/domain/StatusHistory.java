package com.example.A201.history.domain;

import com.example.A201.battery.domain.Battery;
import com.example.A201.common.BaseTime;
import com.example.A201.history.constant.ResultStatus;
import com.example.A201.history.dto.StatusHistoryDTO;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StatusHistory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battery_id")
    private Battery battery;

    @CreatedDate
    private LocalDateTime date;

    @Setter
    @Enumerated(EnumType.STRING)
    private ResultStatus expertStatus;

    @Setter
    private String responseReason;

    @Setter
    private String requestReason;

    public static StatusHistory registerHistory(StatusHistoryDTO statusHistoryDTO,Battery battery){
        StatusHistory history = new StatusHistory();
        history.battery = battery;
        history.expertStatus = statusHistoryDTO.getExpertStatus();
        history.date = LocalDateTime.now();
        history.requestReason = statusHistoryDTO.getReason();
        return history;
    }

}
