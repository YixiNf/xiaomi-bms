package com.xiaomi.bms.domain.model;

import lombok.Data;

@Data
public class Rule {
    private int id;
    private int ruleNumber;
    private String name;
    private String batteryType;
    private String warningRule;
}