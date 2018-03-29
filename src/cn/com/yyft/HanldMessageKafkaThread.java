package cn.com.yyft;

import cn.com.yyft.service.KafkaService;
import cn.com.yyft.utils.JedisUtils;
import cn.com.yyft.utils.KafkaUtils;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.producer.Producer;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;

/**
 * Created by sumenghu on 2016/11/10.
 */
public class HanldMessageKafkaThread implements Runnable {
    Logger logger = Logger.getLogger(HanldMessageKafkaThread.class);
    private KafkaService service;

    private KafkaStream<byte[], byte[]> kafkaStream = null;

    public HanldMessageKafkaThread(KafkaStream<byte[], byte[]> kafkaStream,KafkaService service) {
        super();
        this.kafkaStream = kafkaStream;
        this.service = service;
    }

    public void run() {
        try {
            logger.info("into HanldMessageKafkaThread run");
            ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
            Producer<Integer, String> producer = KafkaUtils.createProducer();
            JedisPool jedisPool = JedisUtils.getJedisPool();
            while (iterator.hasNext()) {
                String message = new String(iterator.next().message());
                service.store(message,producer,jedisPool);
            }
            producer.close();
        }catch (Exception e){
            logger.info("HanldMessageKafkaThread“Ï≥££∫ "+e);
            e.printStackTrace();
        }
    }
}
