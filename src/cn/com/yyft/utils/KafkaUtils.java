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
        //����������û�ȡmetadata,������kafka��Ⱥ�ϵ�����broker,���������������
        //props.put("metadata.broker.list", "hadoopmaster:9092,hadoopslave1:9092,hadoopslave2:9092");
        props.put("metadata.broker.list", "192.168.20.176:9092");
        //��Ϣ���ݵ�brokerʱ�����л���ʽ
        props.put("serializer.class", StringEncoder.class.getName());
        //zk��Ⱥ
        props.put("zookeeper.connect", "192.168.20.176:2181");
        //�Ƿ��ȡ����
        //0�ǲ���ȡ����(��Ϣ�п��ܴ���ʧ��)
        //1�ǻ�ȡ��Ϣ���ݸ�leader����(���������п��ܽ�����Ϣʧ��)
        //-1������in-sync replicas���ܵ���Ϣʱ�ķ���
        props.put("request.required.acks", "1");
        //props.put("partitioner.class", MyPartition.class.getName());

        //����Kafka��������, key����Ϣ��key������, value����Ϣ������
        Producer<Integer, String> producer = new Producer<Integer, String>(
                new ProducerConfig(props));
        return producer;
    }

    public static void sendMessage(Producer<Integer, String> producer,String message, String topic){
        KeyedMessage<Integer, String> keyedMessage = new KeyedMessage<Integer, String>(topic, message);
        producer.send(keyedMessage);
    }
}
