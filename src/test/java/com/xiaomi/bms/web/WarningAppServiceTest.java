package com.xiaomi.bms.web;

import com.xiaomi.bms.application.WarningAppService;
import com.xiaomi.bms.domain.model.ReportRequest;
import com.xiaomi.bms.domain.model.Warning;
import com.xiaomi.bms.domain.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WarningAppServiceTest {

    @Autowired
    private WarningAppService warningAppService;

    @MockBean
    private RuleRepository ruleRepository;

    @Test
    public void testReportSignals() throws Exception {
        List<ReportRequest> requests = new ArrayList<>();

        // 构建测试数据
        ReportRequest request1 = new ReportRequest();
        request1.setCarId(1);
        request1.setWarnId(1);
        request1.setSignal("{\"Mx\":12.0,\"Mi\":0.6}");
        requests.add(request1);

        ReportRequest request2 = new ReportRequest();
        request2.setCarId(2);
        request2.setWarnId(2);
        request2.setSignal("{\"Ix\":12.0,\"Ii\":11.7}");
        requests.add(request2);

        ReportRequest request3 = new ReportRequest();
        request3.setCarId(3);
        request3.setSignal("{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}");
        requests.add(request3);

        // 模拟规则查询结果
        com.xiaomi.bms.domain.model.Rule rule1 = new com.xiaomi.bms.domain.model.Rule();
        rule1.setRuleNumber(1);
        rule1.setName("电压差报警");
        rule1.setBatteryType("三元电池");
        rule1.setWarningRule("...");

        com.xiaomi.bms.domain.model.Rule rule2 = new com.xiaomi.bms.domain.model.Rule();
        rule2.setRuleNumber(2);
        rule2.setName("电流差报警");
        rule2.setBatteryType("三元电池");
        rule2.setWarningRule("...");

        Mockito.when(ruleRepository.getRuleByWarnId(1)).thenReturn(rule1);
        Mockito.when(ruleRepository.getRuleByWarnId(2)).thenReturn(rule2);

        // 调用服务方法
        List<Warning> warnings = warningAppService.reportSignals(requests);

        // 验证结果
        System.out.println(warnings);
        assertNotNull(warnings);
    }
}