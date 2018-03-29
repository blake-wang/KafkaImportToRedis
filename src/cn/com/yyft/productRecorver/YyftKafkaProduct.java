package cn.com.yyft.productRecorver;

import cn.com.yyft.utils.KafkaUtils;
import com.oracle.jrockit.jfr.Producer;
import kafka.javaapi.producer.Producer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by JSJSB-0071 on 2016/11/30.
 */
public class YyftKafkaProduct {
    public static void main(String[] args) throws Exception {
        Producer<Integer, String> producer = KafkaUtils.createProducer();

        File file = new File(args[0]);
        for (File file1 : file.listFiles()) {
            BufferedReader reader = null;
            try {
                System.out.println("the file is: "+file1.getName());
                reader = new BufferedReader(new FileReader(file1));
                String tempString = null;
                int line = 1;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    // 显示行号
                    //System.out.println("line " + line + ": " + tempString);
                    //if(tempString !=null && (tempString.contains("2016-12-01 ") || tempString.contains("01/Dec/2016:"))){
                    KafkaUtils.sendMessage(producer, tempString, args[1]);
                    //}
                    Thread.sleep(5);
                    line++;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }


        KafkaUtils.sendMessage(producer,"",args[1]);

    }
}
