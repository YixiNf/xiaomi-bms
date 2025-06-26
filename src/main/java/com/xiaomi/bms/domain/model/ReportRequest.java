package com.xiaomi.bms.domain.model;

import lombok.Data;

@Data
public class ReportRequest {
    private int carId;
    private int warnId;
    private String signal;
}