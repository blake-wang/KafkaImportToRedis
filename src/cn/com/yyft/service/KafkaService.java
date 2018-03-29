package cn.com.yyft.service;

import kafka.javaapi.producer.Producer;
import redis.clients.jedis.JedisPool;

/**
 * Created by sumenghu on 2016/11/10.
 */
public interface KafkaService {
    void store(String message, Producer<Integer, String> producer,JedisPool jedisPool);
}
