package com.csl.common;


import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import javax.annotation.Resource;

//redis工具类
@Configuration
public class RedisUtil {

    @Resource
    private RedisTemplate<String, String> redisTemplate1;

    //获取缓存
    public String get(final String key){
        return redisTemplate1.opsForValue().get(key);
    }
    //添加缓存
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate1.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //删除缓存
    public boolean delete( final String key){
        boolean result = false;
        try {
            redisTemplate1.delete(key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //减库存
    public void decr(final String key){

    }
}
