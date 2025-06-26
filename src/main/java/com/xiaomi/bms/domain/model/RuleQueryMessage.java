package com.xiaomi.bms.domain.model;

import lombok.Data;

@Data
public class RuleQueryMessage {
    private Integer warnId;
    private String batteryType;
}