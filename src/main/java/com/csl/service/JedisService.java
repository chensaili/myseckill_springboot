package com.csl.service;

public interface JedisService {
    String set(String key,String value);
    long incrBy(String key, long increament);
    long decrBy(String key, long increament);
}
