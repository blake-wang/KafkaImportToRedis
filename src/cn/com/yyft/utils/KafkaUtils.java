package cn.com.yyft.utils;


import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

/**
 * Created by sumenghu on 2016/11/10.
 */
public class KafkaUtils {


    public static Producer<Integer, String> createProducer() {
        Properties props = new Properties();
        //根据这个配置获取metadata,不必是kafka集群上的所有broker,但最好至少有两个
        //props.put("metadata.broker.list", "hadoopmaster:9092,hadoopslave1:9092,hadoopslave2:9092");
        props.put("metadata.broker.list", "192.168.20.176:9092");
        //消息传递到broker时的序列化方式
        props.put("serializer.class", StringEncoder.class.getName());
        //zk集群
        props.put("zookeeper.connect", "192.168.20.176:2181");
        //是否获取反馈
        //0是不获取反馈(消息有可能传输失败)
        //1是获取消息传递给leader后反馈(其他副本有可能接受消息失败)
        //-1是所有in-sync replicas接受到消息时的反馈
        props.put("request.required.acks", "1");
        //props.put("partitioner.class", MyPartition.class.getName());

        //创建Kafka的生产者, key是消息的key的类型, value是消息的类型
        Producer<Integer, String> producer = new Producer<Integer, String>(
                new ProducerConfig(props));
        return producer;
    }

    public static void sendMessage(Producer<Integer, String> producer,String message, String topic){
        KeyedMessage<Integer, String> keyedMessage = new KeyedMessage<Integer, String>(topic, message);
        producer.send(keyedMessage);
    }
}
