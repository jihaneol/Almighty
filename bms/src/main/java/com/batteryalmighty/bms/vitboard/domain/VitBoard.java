package com.batteryalmighty.bms.vitboard.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "vitboard")
public class VitBoard {

//    @Transient
//    public static final String SEQUENCE_NAME = "vit_sequence";

    @Id
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

//    private Long idx;

    @Field(name = "Voltage_measured")
    private Double voltage;

    @Field(name = "Current_measured")
    private Double current;

    @Field(name = "Temperature_measured")
    private Double temperature;

    @Field(name = "Time")
    private Double time;

    @Field(name = "Soc")
    private Double soc;

    @Field(name = "Progress_id")
    private Long progressId;

//    @Field(name = "Date_time")
//    @CreatedDate
//    private LocalDateTime createdDate;

    public void predictEkf(Double ekf){
        this.soc = ekf;
    }
}
