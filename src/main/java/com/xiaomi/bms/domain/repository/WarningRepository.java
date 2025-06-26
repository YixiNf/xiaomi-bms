package com.xiaomi.bms.domain.repository;

import com.xiaomi.bms.domain.model.Warning;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WarningRepository {
    @Select("SELECT id, car_id, warn_name, warn_level, warning_time FROM warning_info WHERE car_id = #{carId} ORDER BY warning_time DESC")
    List<Warning> getWarningsByCarId(int carId);

    @Insert("INSERT INTO warning_info (car_id, warn_name, warn_level, warning_time) VALUES (#{carId}, #{warnName}, #{warnLevel}, NOW())")
    void saveWarning(Warning warning);
}