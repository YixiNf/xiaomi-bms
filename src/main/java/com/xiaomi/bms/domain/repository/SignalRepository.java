package com.xiaomi.bms.domain.repository;

import com.xiaomi.bms.domain.model.Signal;
import com.xiaomi.bms.domain.model.SignalWithHealth;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SignalRepository {

    @Select("SELECT " +
            "vs.id, " +
            "vs.car_id, " +
            "vs.`signal`, " +
            "vs.report_time, " +
            "vi.battery_health " +
            "FROM " +
            "vehicle_signals vs " +
            "JOIN " +
            "vehicle_info vi ON vs.car_id = vi.id " +
            "WHERE " +
            "vs.car_id = #{carId} " +
            "ORDER BY " +
            "vs.report_time DESC")
    List<SignalWithHealth> getSignalsWithHealthByCarId(int carId);

    @Insert("INSERT INTO vehicle_signals (car_id, `signal`, report_time) " +
            "VALUES (#{carId}, CONCAT('{\"Mx\":', #{signal.mx}, ',\"Mi\":', #{signal.mi}, ',\"Ix\":', #{signal.ix}, ',\"Ii\":', #{signal.ii}, '}'), NOW())")
    void saveSignal(@Param("carId") int carId, @Param("signal") Signal signal);
}