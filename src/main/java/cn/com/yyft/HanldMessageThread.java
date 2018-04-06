package cn.com.yyft;

import cn.com.yyft.service.RedisService;
import cn.com.yyft.utils.JedisUtils;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

class HanldMessageThread implements Runnable {
    Logger logger = Logger.getLogger(HanldMessageThread.class);

    private RedisService service;
    private KafkaStream<byte[], byte[]> kafkaStream = null;

    public HanldMessageThread(KafkaStream<byte[], byte[]> kafkaStream, RedisService service) {
        super();
        this.kafkaStream = kafkaStream;
        this.service = service;
    }

    public void run() {
        try {
            logger.info("into HanldMessageThread run");
            //JedisPool jedisPool = JedisUtils.getJedisPool();
            ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
            while (iterator.hasNext()) {
                String message = new String(iterator.next().message());
                service.store(message);
            }
        } catch (Exception e) {
            logger.error("异常",e);
            e.printStackTrace();
        }
    }

}
