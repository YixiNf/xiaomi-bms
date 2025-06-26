package com.xiaomi.bms.web;

import java.sql.*;

public class JdbcTest {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/xiaomi?useUnicode=true&characterEncoding=utf8",
                "root", "11111111")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM warning_rules WHERE rule_number = 1");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", 电池类型: " + rs.getString("battery_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}