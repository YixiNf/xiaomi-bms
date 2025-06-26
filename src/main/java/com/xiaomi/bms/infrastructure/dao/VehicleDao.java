package com.xiaomi.bms.infrastructure.dao;

import com.xiaomi.bms.domain.model.Vehicle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VehicleDao {
    Vehicle getVehicleById(int id);
    void insertVehicle(Vehicle vehicle);
}