package com.example.A201.board.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.persistence.Id;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "vitboard")
public class VitBoard {

    @Id
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

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

}