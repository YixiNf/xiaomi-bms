package com.xiaomi.bms.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaomi.bms.domain.model.*;
import com.xiaomi.bms.domain.repository.RuleRepository;
import com.xiaomi.bms.domain.repository.SignalRepository;
import com.xiaomi.bms.domain.repository.VehicleRepository;
import com.xiaomi.bms.domain.repository.WarningRepository;
import com.xiaomi.bms.domain.service.WarningService;
import com.xiaomi.bms.infrastructure.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WarningAppServiceImpl implements WarningAppService {

    @Autowired
    private WarningService warningService;

    @Autowired
    private SignalAppService signalAppService;

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WarningRepository warningRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RuleRepository ruleRepository;

    /**
     * 处理车辆信号上报并生成预警
     */
    @Override
    @Transactional
    public List<Warning> reportSignals(List<ReportRequest> requests) {
        List<Warning> warnings = new ArrayList<>();
        for (ReportRequest request : requests) {
            try {
                // 1. 解析信号JSON为Signal对象
                Signal signal = objectMapper.readValue(request.getSignal(), Signal.class);
//                System.out.println("request: " + request);
//                System.out.println("signal: " + signal);
                // 2. 获取电池类型
                int carId = request.getCarId();
                int warnId = request.getWarnId();
                String batteryType = getBatteryType(carId);
//                testQuery(warnId);
//                testDirectJdbc(warnId);
                // 3. 根据warnId或batteryType获取规则
                List<Rule> rules = new ArrayList<>();
//                System.out.println("warn_id:" + warnId + ",batteryType:" + batteryType);
                if (warnId != 0) {
                    Rule rule = getRuleByWarnIdAndBatteryType(warnId, batteryType);
//                    System.out.println("rule:" + rule);
                    if (rule != null) {
                        rules.add(rule);
                    }
                } else {
                    rules = getRulesByBatteryType(batteryType);
                }
//                System.out.println("rules:" + rules);
                // 4. 检查预警
                for (Rule rule : rules) {
//                    System.out.println("rule: " + rule.getWarningRule());
                    List<Warning> newWarnings = warningService.checkWarnings(carId, warnId, rule, signal);
                    warnings.addAll(newWarnings);
                    System.out.println("warnings: " + warnings);
                    // 保存预警信息到数据库
                    for (Warning warning : newWarnings) {
                        warningRepository.saveWarning(warning);
                    }
                }

                // 5. 保存原始信号数据到数据库
                signalAppService.saveSignal(carId, signal);


            } catch (IOException e) {
                throw new RuntimeException("信号解析失败", e);
            }
        }
        return warnings;
    }
    public void testQuery(int warnId) {
        // 清除可能的缓存
        redisService.delete("vehicle:" + warnId);

        List<Rule> rules = ruleRepository.getTest(warnId);
        System.out.println("查询结果数量: " + rules.size());
    }
    @Autowired
    private DataSource dataSource;

    public void testDirectJdbc(int warnId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, rule_number, battery_type FROM warning_rules WHERE rule_number = ?")) {
            stmt.setInt(1, warnId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", 电池类型: " + rs.getString("battery_type"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Rule getRuleByWarnIdAndBatteryType(int warnId, String batteryType) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule FROM warning_rules WHERE rule_number = ? AND battery_type = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warnId);
            stmt.setString(2, batteryType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRule(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<Rule> getRulesByBatteryType(String batteryType) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule FROM warning_rules WHERE TRIM(battery_type) = TRIM(?)";
        List<Rule> rules = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, batteryType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapRule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rules;
    }

    private List<Rule> getRulesByWarnId(int warnId) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule " +
                "FROM warning_rules WHERE rule_number = ?";
        List<Rule> rules = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warnId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapRule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rules;
    }

    private Rule mapRule(ResultSet rs) throws SQLException {
        Rule rule = new Rule();
        rule.setId(rs.getInt("id"));
        rule.setRuleNumber(rs.getInt("ruleNumber"));
        rule.setName(rs.getString("name"));
        rule.setBatteryType(rs.getString("batteryType"));
        rule.setWarningRule(rs.getString("warningRule"));
        return rule;
    }

    private String getBatteryType(int carId) {
        // 先从Redis缓存中获取车辆信息
//        Vehicle vehicle1 = vehicleRepository.getVehicleById(1);
//        System.out.println("vehicle: " + vehicle1);
        String key = "vehicle:" + carId;
        Object cachedVehicle = redisService.get(key);
        System.out.println("Cache:" + cachedVehicle);
        if (cachedVehicle != null) {
            System.out.println("In Cache");
            Vehicle vehicle = (Vehicle) cachedVehicle;
            return vehicle.getBatteryType();
        }
        // 如果缓存中没有，从数据库中获取
        System.out.println("Not in Cache Get carId: " + carId);
        Vehicle vehicle = vehicleRepository.getVehicleById(carId);
        System.out.println("Vehicle:" + vehicle);
        if (vehicle != null) {
            // 将车辆信息存入Redis缓存
            redisService.set(key, vehicle, 60, java.util.concurrent.TimeUnit.MINUTES);
            return vehicle.getBatteryType();
        }
        return null;
    }

    /**
     * 查询车辆的历史信号数据
     */
    @Override
    public List<SignalWithHealth> getSignalsWithHealthByCarId(int carId) {
        return signalRepository.getSignalsWithHealthByCarId(carId);
    }

    /**
     * 查询车辆的历史预警信息
     */
    @Override
    public List<Warning> getWarningsByCarId(int carId) {
        return warningRepository.getWarningsByCarId(carId);
    }
}