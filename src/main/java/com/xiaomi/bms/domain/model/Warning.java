package com.xiaomi.bms.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class Warning {
    private int carId;
    private String warnName;
    private int warnLevel;
    private Date warningTime;
}