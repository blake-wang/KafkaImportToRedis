package cn.com.yyft.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by sumenghu on 2016/11/9.
 */
public interface RedisService {
    void store(String message);
}
