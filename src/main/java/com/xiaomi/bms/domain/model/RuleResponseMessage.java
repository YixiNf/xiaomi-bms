package com.xiaomi.bms.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class RuleResponseMessage {
    private Integer warnId;
    private List<Rule> rules;
}