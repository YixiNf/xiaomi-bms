package com.xiaomi.bms.domain.repository;

import com.xiaomi.bms.domain.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RuleRepository_jdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 注入 DataSource 进行验证
    @Autowired
    private DataSource dataSource;

    public void printDataSourceInfo() {
        System.out.println("JdbcTemplate 数据源: " + jdbcTemplate.getDataSource());
        System.out.println("直接注入数据源: " + dataSource);
    }
    public Rule getRuleByWarnIdAndBatteryType(int warnId, String batteryType) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule FROM warning_rules WHERE rule_number = ? AND battery_type = ?";
        List<Rule> rules = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Rule.class), warnId, batteryType);
        return rules.isEmpty() ? null : rules.get(0);
    }

    public Rule getRuleByWarnId(int warnId) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule FROM warning_rules WHERE rule_number = ?";
        List<Rule> rules = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Rule.class), warnId);
        return rules.isEmpty() ? null : rules.get(0);
    }

    public List<Rule> getTest(int warnId) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule " +
                "FROM warning_rules WHERE rule_number = ?";

        // 显式创建 RowMapper
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Rule rule = new Rule();
            rule.setId(rs.getInt("id"));
            rule.setRuleNumber(rs.getInt("ruleNumber"));
            rule.setName(rs.getString("name"));
            rule.setBatteryType(rs.getString("batteryType"));
            rule.setWarningRule(rs.getString("warningRule"));
            return rule;
        }, warnId);
    }

    public List<Rule> getRulesByBatteryType(String batteryType) {
        String sql = "SELECT id, rule_number as ruleNumber, name, battery_type as batteryType, warning_rule as warningRule FROM warning_rules WHERE TRIM(battery_type) = TRIM(?)";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Rule.class), batteryType);
    }

    public void saveRule(Rule rule) {
        String sql = "INSERT INTO warning_rules (rule_number, name, battery_type, warning_rule) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, rule.getRuleNumber(), rule.getName(), rule.getBatteryType(), rule.getWarningRule());
    }
}