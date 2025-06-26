package com.xiaomi.bms.infrastructure.dao;

import com.xiaomi.bms.domain.model.Signal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SignalDao {
    List<Signal> getSignalsByCarId(int carId);
    void insertSignal(int carId, String signalJson);
}