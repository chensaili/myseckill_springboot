package com.csl.service.impl;

import com.csl.service.JedisService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

public class JedisServiceImpl implements JedisService {

    @Autowired
    private JedisPool jedisPool;

    @Override
    public String set(String key, String value) {
        return null;
    }

    @Override
    public long incrBy(String key, long increament) {
        return 0;
    }

    @Override
    public long decrBy(String key, long increament) {
        return 0;
    }
}
