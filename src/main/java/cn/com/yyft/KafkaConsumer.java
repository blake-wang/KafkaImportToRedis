package cn.com.yyft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.yyft.service.*;
import cn.com.yyft.service.kafkaimpl.ActiveService;
import cn.com.yyft.service.redisimpl.BinduidService;
import cn.com.yyft.service.redisimpl.MemberServiceImpl;
import cn.com.yyft.service.redisimpl.OrderServiceImpl;
import cn.com.yyft.service.redisimpl.RegiServiceImpl;
import cn.com.yyft.utils.PropertiesHelp;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.log4j.Logger;

public class KafkaConsumer extends Thread {


    private static final int THREAD_AMOUNT = 2;

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.load(KafkaConsumer.class.getResourceAsStream("/resources/consumer1.properties"));

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        // 每个topic使用多少个kafkastream读取, 多个consumer
        String topics = PropertiesHelp.getRelativePathValue("topics");
        String[] topicSplits = topics.split(",");
        for (int i = 0; i < topicSplits.length; i++) {
            topicCountMap.put(topicSplits[i], THREAD_AMOUNT);
        }
        //KafkaImportToredisGroup1_test

        // 可以读取多个topic
        // topicCountMap.put(TOPIC2, 1);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams = consumer.createMessageStreams(topicCountMap);

        //消费Topic Start
        String isHotCloud = PropertiesHelp.getRelativePathValue("is_hot_cloud");
        if("1".equals(isHotCloud)){
            consumerActiveTipic(msgStreams);
        } else {
            consumerMemberTopic(msgStreams);
            consumerRegiTopic(msgStreams);
            consumerBindUidTopic(msgStreams);
            consumerOrderTopic(msgStreams);
        }

    }

    private static void consumerOrderTopic(Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams) {
        List<KafkaStream<byte[], byte[]>> bindUidMsgStreamList = msgStreams.get("order");
        RedisService orderService = new OrderServiceImpl();
        ExecutorService bindUidExecutor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        for (int i = 0; i < bindUidMsgStreamList.size(); i++) {
            KafkaStream<byte[], byte[]> kafkaStream = bindUidMsgStreamList.get(i);
            bindUidExecutor.submit(new HanldMessageThread(kafkaStream, orderService));
        }
    }

    private static void consumerActiveTipic(Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams) {
        List<KafkaStream<byte[], byte[]>> activeMsgStreamList = msgStreams.get("active");
        KafkaService service = new ActiveService();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        for (int i = 0; i < activeMsgStreamList.size(); i++) {
            KafkaStream<byte[], byte[]> kafkaStream = activeMsgStreamList.get(i);
            executor.submit(new HanldMessageKafkaThread(kafkaStream,service));
        }
    }

    private static void consumerBindUidTopic(Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams) {
        List<KafkaStream<byte[], byte[]>> bindUidMsgStreamList = msgStreams.get("binduid");
        RedisService bindUidService = new BinduidService();
        ExecutorService bindUidExecutor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        for (int i = 0; i < bindUidMsgStreamList.size(); i++) {
            KafkaStream<byte[], byte[]> kafkaStream = bindUidMsgStreamList.get(i);
            bindUidExecutor.submit(new HanldMessageThread(kafkaStream, bindUidService));
        }
    }

    private static void consumerMemberTopic(Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams) {
        List<KafkaStream<byte[], byte[]>> memberMsgStreamList = msgStreams.get("member");
        RedisService memberService = new MemberServiceImpl();
        ExecutorService memberExecutor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        for (int i = 0; i < memberMsgStreamList.size(); i++) {
            KafkaStream<byte[], byte[]> kafkaStream = memberMsgStreamList.get(i);
            memberExecutor.submit(new HanldMessageThread(kafkaStream, memberService));
        }
    }

    private static void consumerRegiTopic(Map<String, List<KafkaStream<byte[], byte[]>>> msgStreams) {
        List<KafkaStream<byte[], byte[]>> regiMsgStreamList = msgStreams.get("regi");
        // 使用ExecutorService来调度线程
        ExecutorService regiExecutor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        RedisService regiService = new RegiServiceImpl();
        for (int i = 0; i < regiMsgStreamList.size(); i++) {
            KafkaStream<byte[], byte[]> kafkaStream = regiMsgStreamList.get(i);
            regiExecutor.submit(new HanldMessageThread(kafkaStream, regiService));
        }
    }
}