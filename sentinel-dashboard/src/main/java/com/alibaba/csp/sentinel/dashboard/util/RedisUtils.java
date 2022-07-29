package com.alibaba.csp.sentinel.dashboard.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: asong
 * @time: 2022/7/29
 */
@Component
public class RedisUtils {



    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    /**
     * 存放string类型
     *
     * @param key
     * @param data
     * @param timeout
     */
    public void setString(String key, String data, Long timeout) {
        try {
            stringRedisTemplate.opsForValue().set(key, data);
            if (timeout != null) {
                stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 存放string类型
     *
     * @param key
     * @param data
     */
    public void setString(String key, String data) {
        setString(key, data, null);
    }

    /**
     * 根据key查询string类型
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return value;
    }

    /**
     * 根据对应的key删除key
     *
     * @param key
     */
    public Boolean delKey(String key) {
        return stringRedisTemplate.delete(key);
    }

}
