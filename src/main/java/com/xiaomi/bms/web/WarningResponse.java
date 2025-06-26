package com.xiaomi.bms.web;

import com.xiaomi.bms.domain.model.Warning;
import lombok.Data;

import java.util.List;

@Data
public class WarningResponse {
    private int status = 200;
    private String msg = "ok";
    private List<Warning> data;

    public WarningResponse(List<Warning> data) {
        this.data = data;
    }
}