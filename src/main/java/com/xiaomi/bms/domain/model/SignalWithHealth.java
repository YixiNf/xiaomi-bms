package com.xiaomi.bms.domain.model;

import lombok.Data;

@Data
public class SignalWithHealth {
    private int id;
    private int carId;
    private String signal;
    private java.util.Date reportTime;
    private int batteryHealth;
}