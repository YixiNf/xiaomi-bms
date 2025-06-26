package com.xiaomi.bms.web;

import com.xiaomi.bms.application.WarningAppService;
import com.xiaomi.bms.domain.model.ReportRequest;
import com.xiaomi.bms.domain.model.Signal;
import com.xiaomi.bms.domain.model.SignalWithHealth;
import com.xiaomi.bms.domain.model.Warning;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class WarningController {

    private WarningAppService warningAppService;

    public WarningController(WarningAppService warningAppService) {
        this.warningAppService = warningAppService;
    }

    @PostMapping("/api/warn")
    public WarningResponse reportSignals(@RequestBody List<ReportRequest> requests) {
        List<Warning> warnings = warningAppService.reportSignals(requests);
        return new WarningResponse(warnings);
    }

    @GetMapping("/api/signals/{carId}")
    public List<SignalWithHealth> getSignalsByCarId(@PathVariable int carId) {
        return warningAppService.getSignalsWithHealthByCarId(carId);
    }

    @GetMapping("/api/warnings/{carId}")
    public List<Warning> getWarningsByCarId(@PathVariable int carId) {
        return warningAppService.getWarningsByCarId(carId);
    }
}