package cn.com.yyft.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisUtils {

	public static JedisPool getJedisPool() {
		String host = PropertiesHelp.getRelativePathValue("redis.host");
		int port = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.port"));
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		// 最大空闲连接数
		poolConfig.setMaxIdle(Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.idle")));
		// 连接池的最大连接数
		poolConfig.setMaxTotal(Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.total")));
		// 设置获取连接的最大等待时间
		poolConfig.setMaxWaitMillis(Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.wait.millis")));
		// 从连接池中获取连接的时候是否需要校验，这样可以保证取出的连接都是可用的
		poolConfig.setMinIdle(8);//设置最小空闲数
		poolConfig.setTestOnBorrow(true);

		poolConfig.setTestOnReturn(true);
		//Idle时进行连接扫描
		poolConfig.setTestWhileIdle(true);
		//表示idle object evitor两次扫描之间要sleep的毫秒数
		poolConfig.setTimeBetweenEvictionRunsMillis(30000);
		//表示idle object evitor每次扫描的最多的对象数
		poolConfig.setNumTestsPerEvictionRun(10);
		//表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		poolConfig.setMinEvictableIdleTimeMillis(60000);

		// 获取jedis连接池
		JedisPool pool = new JedisPool(poolConfig, host, port,30000,"redis");
		return pool;
	}

	public static Jedis getJedis(JedisPool jedisPool) {
		Jedis jedis;
		while (true){
			jedis = jedisPool.getResource();
			if(jedis !=null){
				return jedis;
			}
		}
	}
}
