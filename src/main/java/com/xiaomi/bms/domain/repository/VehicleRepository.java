package com.xiaomi.bms.domain.repository;

import com.xiaomi.bms.domain.model.Vehicle;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VehicleRepository {
    @Select("SELECT id, vid, frame_number, battery_type, total_mileage, battery_health FROM vehicle_info WHERE frame_number = #{id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "vid", property = "vid"),
            @Result(column = "frame_number", property = "frameNumber"),
            @Result(column = "battery_type", property = "batteryType"),
            @Result(column = "total_mileage", property = "totalMileage"),
            @Result(column = "battery_health", property = "batteryHealth")
    })
    Vehicle getVehicleById(int id);

    @Insert("INSERT INTO vehicle_info (vid, frame_number, battery_type, total_mileage, battery_health) VALUES (#{vid}, #{frameNumber}, #{batteryType}, #{totalMileage}, #{batteryHealth})")
    void saveVehicle(Vehicle vehicle);

    @Update("UPDATE vehicle_info SET vid = #{vid}, frame_number = #{frameNumber}, battery_type = #{batteryType}, total_mileage = #{totalMileage}, battery_health = #{batteryHealth} WHERE id = #{id}")
    void updateVehicle(Vehicle vehicle);

    @Delete("DELETE FROM vehicle_info WHERE frame_number = #{id}")
    void deleteVehicle(int id);

    @Select("SELECT id, vid, frame_number, battery_type, total_mileage, battery_health FROM vehicle_info")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "vid", property = "vid"),
            @Result(column = "frame_number", property = "frameNumber"),
            @Result(column = "battery_type", property = "batteryType"),
            @Result(column = "total_mileage", property = "totalMileage"),
            @Result(column = "battery_health", property = "batteryHealth")
    })
    List<Vehicle> getAllVehicles();
}