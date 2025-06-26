package com.xiaomi.bms.infrastructure.dao;

import com.xiaomi.bms.domain.model.Warning;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WarningDao {
    List<Warning> getWarningsByCarId(int carId);
    void insertWarning(Warning warning);
}