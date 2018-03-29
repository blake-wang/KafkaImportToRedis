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

    //Redis������IP
    private static String ADDR_ARRAY = PropertiesHelp.getRelativePathValue("redis.host");

    //Redis�Ķ˿ں�
    private static int PORT = new Integer(PropertiesHelp.getRelativePathValue("redis.port"));

    //��������
//    private static String AUTH = FileUtil.getPropertyValue("/properties/redis.properties", "auth");

    //��������ʵ���������Ŀ��Ĭ��ֵΪ8��
    //�����ֵΪ-1�����ʾ�����ƣ����pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted(�ľ�)��
    private static int MAX_TOTAL = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.total"));

    //����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����Ĭ��ֵҲ��8��
    private static int MAX_IDLE = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.idle"));

    //�ȴ��������ӵ����ʱ�䣬��λ���룬Ĭ��ֵΪ-1����ʾ������ʱ����������ȴ�ʱ�䣬��ֱ���׳�JedisConnectionException��
    private static int MAX_WAIT = Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.max.wait.millis"));

    //��ʱʱ��
    private static int TIMEOUT = 30000;//FileUtil.getPropertyValueInt("/properties/redis.properties", "timeout");;

    //��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���jedisʵ�����ǿ��õģ�
    private static boolean TEST_ON_BORROW = true;//FileUtil.getPropertyValueBoolean("/properties/redis.properties", "test_on_borrow");;

    private static JedisPool jedisPool = null;

    /**
     * ��ʼ��Redis���ӳ�
     */
    private static void initialPool(){
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setTestOnReturn(false);
            //Idleʱ��������ɨ��
            config.setTestWhileIdle(true);
            //��ʾidle object evitor����ɨ��֮��Ҫsleep�ĺ�����
            config.setTimeBetweenEvictionRunsMillis(30000);
            //��ʾһ����������ͣ����idle״̬�����ʱ�䣬Ȼ����ܱ�idle object evitorɨ�貢������һ��ֻ����timeBetweenEvictionRunsMillis����0ʱ��������
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
     * �ڶ��̻߳���ͬ����ʼ��
     */
    private static synchronized void poolInit() {
        if (jedisPool == null) {
            initialPool();
        }
    }

    /**
     * ͬ����ȡJedisʵ��
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
     * �ͷ�jedis��Դ
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool !=null) {
            jedisPool.returnResource(jedis);
        }
    }
}
