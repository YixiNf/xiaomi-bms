package com.xiaomi.bms.domain.model;

import lombok.Data;

@Data
public class Vehicle {
    private int id;
    private String vid;
    private int frameNumber;
    private String batteryType;
    private int totalMileage;
    private int batteryHealth;
}