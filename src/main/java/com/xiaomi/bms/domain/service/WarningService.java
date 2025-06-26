package com.xiaomi.bms.domain.service;

import com.xiaomi.bms.domain.model.Rule;
import com.xiaomi.bms.domain.model.Signal;
import com.xiaomi.bms.domain.model.Warning;

import java.util.List;

public interface WarningService {
    List<Warning> checkWarnings(int carId, int warnId, Rule rule, Signal signal);
}