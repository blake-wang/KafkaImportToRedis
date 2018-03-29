package cn.com.yyft.service.kafkaimpl;

import cn.com.yyft.service.KafkaService;
import cn.com.yyft.utils.JedisUtils;
import cn.com.yyft.utils.KafkaUtils;
import kafka.javaapi.producer.Producer;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by sumenghu on 2016/11/10.
 */
public class ActiveService implements KafkaService {
    Logger logger = Logger.getLogger(ActiveService.class);

    @Override
    public void store(String message, Producer<Integer, String> producer, JedisPool jedisPool) {
        //2016-11-10 15:22:55,075 [INFO] login: bi_active|70|18|||2016-11-10 15:22:55|117.44.160.113|ANDROID|896832891597117
        Jedis jedis = null;
        try {
            jedis = JedisUtils.getJedis(jedisPool);

            logger.info("into thread message：" + message);
            String[] splited = message.split("\\|", -1);

            String gameId = splited[1];//游戏id
            String imei = splited[8];//imei号
            String key = gameId + "-" + imei;


            jedis.select(1);

            if (jedis.get(key) == null) {
                jedis.set(key, "1");
                //store kafka to firstOpenActive topic
                String topic = "firstOpenActive";
                KafkaUtils.sendMessage(producer, message, topic);
            }
        } catch (Exception e) {
            logger.info("the jedis: "+jedis);
            logger.error("异常: " + e);
            e.printStackTrace();
        } finally {
            if (null != jedisPool) {
                jedisPool.returnResource(jedis);
            }
        }
    }
}
