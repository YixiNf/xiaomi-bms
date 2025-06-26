package com.xiaomi.bms.web;

import com.xiaomi.bms.application.SignalAppService;
import com.xiaomi.bms.domain.model.Signal;
import com.xiaomi.bms.domain.model.SignalWithHealth;
import com.xiaomi.bms.domain.repository.SignalRepository;
import com.xiaomi.bms.infrastructure.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

class SignalAppServiceTest {

    @Mock
    private SignalRepository signalRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private SignalAppService signalAppService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignalCachingFlow() {
        // 准备测试数据
        int carId = 1;
        String cacheKey = "signals:" + carId;

        // 创建测试信号
        Signal testSignal = new Signal();
        testSignal.setMx(12.0);
        testSignal.setMi(0.6);

        // 创建带健康状态的信号列表
        List<SignalWithHealth> signals = Arrays.asList(
                createSignalWithHealth(1, carId, "{\"Mx\":12.0,\"Mi\":0.6}", 85),
                createSignalWithHealth(2, carId, "{\"Mx\":11.8,\"Mi\":0.7}", 85)
        );

        // 模拟数据库查询结果
        when(signalRepository.getSignalsWithHealthByCarId(carId)).thenReturn(signals);

        // 第一次查询：应从数据库加载并缓存
        System.out.println("=== 第一次查询 ===");
        List<SignalWithHealth> result1 = signalAppService.getSignalsWithHealthByCarId(carId);
        System.out.println("查询结果: " + result1);

        // 验证缓存被设置
        verify(redisService, times(1)).set(eq(cacheKey), eq(signals), anyLong(), any());

//        List<SignalWithHealth> result_check = signalAppService.getSignalsWithHealthByCarId(carId);
        // 打印缓存内容（通过mock验证）
        System.out.println("缓存内容: " + signals);

        // 保存新信号：应清除缓存
        System.out.println("\n=== 保存新信号 ===");
        signalAppService.saveSignal(carId, testSignal);
        System.out.println("已保存信号，缓存应被清除");

        // 验证缓存被删除
        verify(redisService, times(1)).delete(cacheKey);

        // 第二次查询：应再次从数据库加载并重新缓存
        System.out.println("\n=== 第二次查询 ===");
        List<SignalWithHealth> result2 = signalAppService.getSignalsWithHealthByCarId(carId);
        System.out.println("查询结果: " + result2);

        // 验证数据库再次被查询
        verify(signalRepository, times(2)).getSignalsWithHealthByCarId(carId);
    }

    private SignalWithHealth createSignalWithHealth(int id, int carId, String signalJson, int health) {
        SignalWithHealth signal = new SignalWithHealth();
        signal.setId(id);
        signal.setCarId(carId);
        signal.setSignal(signalJson);
        signal.setReportTime(new Date());
        signal.setBatteryHealth(health);
        return signal;
    }
}