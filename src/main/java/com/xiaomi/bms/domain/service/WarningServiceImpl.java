package com.xiaomi.bms.domain.service;

import com.xiaomi.bms.domain.model.Rule;
import com.xiaomi.bms.domain.model.Signal;
import com.xiaomi.bms.domain.model.Warning;
import com.xiaomi.bms.domain.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WarningServiceImpl implements WarningService {

    @Autowired
    private RuleRepository ruleRepository;
    @Override
    public List<Warning> checkWarnings(int carId, int warnId, Rule rule, Signal signal) {
//        // 根据规则编号获取预警规则
//        Rule rule = ruleRepository.getRuleByWarnId(warnId);
        // 解析规则并判断是否触发预警
        List<Warning> warnings = parseRuleAndCheck(rule, signal, carId);
        return warnings;
    }

    private List<Warning> parseRuleAndCheck(Rule rule, Signal signal, int carId) {
        List<Warning> warnings = new ArrayList<>();
        if (rule != null) {
            String warningRule = rule.getWarningRule();
            String[] ruleLines = warningRule.split("\n"); // 按换行符分割

            // 根据规则名称计算相应的差值
            double diff = 0;
            if ("电压差报警".equals(rule.getName())) {
                diff = signal.getMx() - signal.getMi();
            } else if ("电流差报警".equals(rule.getName())) {
                diff = signal.getIx() - signal.getIi();
            }
//            System.out.println("diff: " + diff);
            // 遍历规则行，匹配条件并添加预警
            for (String ruleLine : ruleLines) {
//                System.out.println("ruleLine: " + ruleLine);
                if (ruleLine.contains("不报警")) {
                    continue; // 忽略不报警的规则
                }

                // 更新：优化正则表达式，处理两种条件格式
                Pattern pattern = Pattern.compile(
                        "(?:\\(([^)]+)\\)|([^,]+))<=([^<]+)<([^,]+),报警等级：(\\d+)|" + // 格式1: 下限<=条件<上限,报警等级：X
                                "(?:\\(([^)]+)\\)|([^,]+))<=([^,]+),报警等级：(\\d+)"       // 格式2: 下限<=条件,报警等级：X
                );
                Matcher matcher = pattern.matcher(ruleLine);
                if (matcher.find()) {
                    double lowerBound, upperBound;
                    int warnLevel;

                    // 处理格式1: 下限<=条件<上限,报警等级：X
                    if (matcher.group(1) != null || matcher.group(2) != null) {
                        lowerBound = Double.parseDouble(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
                        upperBound = Double.parseDouble(matcher.group(4));
                        warnLevel = Integer.parseInt(matcher.group(5));
//                        System.out.println("00lowerBound: " + lowerBound + ", upperBound: " + upperBound + ", warnLevel: " + warnLevel);
                        if (diff >= lowerBound && diff < upperBound) {
                            addWarning(warnings, carId, rule.getName(), warnLevel);
                        }
                    }
                    // 处理格式2: 下限<=条件,报警等级：X
                    else if (matcher.group(6) != null || matcher.group(7) != null) {
//                        System.out.println("matcher.group(6): " + matcher.group(6) + " " + matcher.group(7) + " " + matcher.group(8) + " " + matcher.group(9));
                        lowerBound = Double.parseDouble(matcher.group(6) != null ? matcher.group(6) : matcher.group(7));
                        warnLevel = Integer.parseInt(matcher.group(9));
//                        System.out.println("11lowerBound: " + lowerBound + ", warnLevel: " + warnLevel);

                        if (diff >= lowerBound) {
                            addWarning(warnings, carId, rule.getName(), warnLevel);
                        }
                    }
                }
            }
        }
        return warnings;
    }

    private void addWarning(List<Warning> warnings, int carId, String warnName, int warnLevel) {
        Warning warning = new Warning();
        warning.setCarId(carId);
        warning.setWarnName(warnName);
        warning.setWarnLevel(warnLevel);
        warning.setWarningTime(new java.util.Date());
        warnings.add(warning);
    }
}