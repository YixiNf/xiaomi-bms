package com.xiaomi.bms.application;

import com.xiaomi.bms.domain.model.SignalWithHealth;
import com.xiaomi.bms.domain.repository.SignalRepository;
import com.xiaomi.bms.infrastructure.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SignalAppService {

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private RedisService redisService;

    public List<SignalWithHealth> getSignalsWithHealthByCarId(int carId) {
        String key = "signals:" + carId;
        List<SignalWithHealth> signals = (List<SignalWithHealth>) redisService.get(key);
        System.out.println("Redis Cache, key = [" + key + "]: " + redisService.get(key));
        if (signals == null) {
            System.out.println("Not in Cache");
            signals = signalRepository.getSignalsWithHealthByCarId(carId);
            if (signals != null) {
                redisService.set(key, signals, 60, TimeUnit.MINUTES);
            }
        }
        return signals;
    }
    public void saveSignal(int carId, com.xiaomi.bms.domain.model.Signal signal) {
        // 保存信号到数据库
        signalRepository.saveSignal(carId, signal);
        // 清除缓存
        String key = "signals:" + carId;
        redisService.delete(key);
    }
}