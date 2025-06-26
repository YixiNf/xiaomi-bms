package com.xiaomi.bms.application;

import com.xiaomi.bms.domain.model.ReportRequest;
import com.xiaomi.bms.domain.model.Signal;
import com.xiaomi.bms.domain.model.SignalWithHealth;
import com.xiaomi.bms.domain.model.Warning;
import java.util.List;

public interface WarningAppService {
    List<Warning> reportSignals(List<ReportRequest> requests);
    List<SignalWithHealth> getSignalsWithHealthByCarId(int carId);
    List<Warning> getWarningsByCarId(int carId);
}