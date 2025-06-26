package com.xiaomi.bms.domain.repository;

import com.xiaomi.bms.domain.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RuleRepository {

    @Autowired
    private DataSource dataSource;

    public void printDataSourceInfo() {
        System.out.println("直接注入数据源: " + dataSource);
    }

    public Rule getRuleByWarnIdAndBatteryType(int warnId, String batteryType) {
        System.out.println("ID: " + warnId + ", 电池类型: " + batteryType);
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

    public Rule getRuleByWarnId(int warnId) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule FROM warning_rules WHERE rule_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warnId);
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

    public List<Rule> getTest(int warnId) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule " +
                "FROM warning_rules WHERE rule_number = ?";
        List<Rule> rules = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warnId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
//                    System.out.println("ID: " + rs.getInt("id") + ", 电池类型: " + rs.getString("batteryType"));
                    rules.add(mapRule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rules;
    }

    public List<Rule> getRulesByBatteryType(String batteryType) {
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

    public void saveRule(Rule rule) {
        String sql = "INSERT INTO warning_rules (rule_number, name, battery_type, warning_rule) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rule.getRuleNumber());
            stmt.setString(2, rule.getName());
            stmt.setString(3, rule.getBatteryType());
            stmt.setString(4, rule.getWarningRule());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}