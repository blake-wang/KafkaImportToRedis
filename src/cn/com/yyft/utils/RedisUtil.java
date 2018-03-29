package cn.com.yyft.utils;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by JSJSB-0071 on 2017/1/2.
 */
public class RedisUtil {
    protected static Logger logger = Logger.getLogger(RedisUtil.class);

    //Redis服务器IP
    private static String ADDR_ARRAY = PropertiesHelp.getRelativePathValue("redis.host");

    //Redis的端口号
    private static int PORT = new Integer(PropertiesHelp.getRelativePathValue("redis.port"));

    //访问密码
//    private static String AUTH = FileUtil.getPropertyValue("/properties/redis.properties", "auth");

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_TOTAL = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.total"));

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.idle"));

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.wait.millis"));

    //超时时间
    private static int TIMEOUT = 30000;//FileUtil.getPropertyValueInt("/properties/redis.properties", "timeout");;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;//FileUtil.getPropertyValueBoolean("/properties/redis.properties", "test_on_borrow");;

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    private static void initialPool(){
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setTestOnReturn(false);
            //Idle时进行连接扫描
            config.setTestWhileIdle(true);
            //表示idle object evitor两次扫描之间要sleep的毫秒数
            config.setTimeBetweenEvictionRunsMillis(30000);
            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
            config.setMinEvictableIdleTimeMillis(-1);
            config.setSoftMinEvictableIdleTimeMillis(10000);
            config.getJmxEnabled();
            config.setJmxNamePrefix("youyuan");
            config.setBlockWhenExhausted(false);

            jedisPool = new JedisPool(config, ADDR_ARRAY, PORT, TIMEOUT,"redis");
        } catch (Exception e) {
            logger.error("create JedisPool error : "+e);
        }
    }

    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (jedisPool == null) {
            initialPool();
        }
    }

    /**
     * 同步获取Jedis实例
     * @return Jedis
     */
    public synchronized static Jedis getJedis() {
        if (jedisPool == null) {
            poolInit();
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("Get jedis error : "+e);
        }finally{
            returnResource(jedis);
        }
        return jedis;
    }


    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool !=null) {
            jedisPool.returnResource(jedis);
        }
    }
}
