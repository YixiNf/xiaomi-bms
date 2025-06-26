package com.xiaomi.bms;

import com.xiaomi.bms.domain.model.Rule;
import com.xiaomi.bms.domain.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BmsApplicationTests {
    @Autowired
    private RuleRepository ruleRepository;

    public void testQuery() {
        List<Rule> rules = ruleRepository.getTest(1);
        System.out.println("查询结果数量: " + (rules != null ? rules.size() : "null"));
        if (rules != null) {
            for (Rule rule : rules) {
                System.out.println("规则: " + rule.getBatteryType());
            }
        }
    }
}
