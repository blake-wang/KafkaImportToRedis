package cn.com.yyft.productRecorver;

import cn.com.yyft.utils.KafkaUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by JSJSB-0071 on 2016/12/2.
 */
public class OrderTest {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = null;
        try {
            File file1 = new File("C:\\Users\\JSJSB-0071\\Desktop\\logs\\order.txt");
            System.out.println("the file is: " + file1.getName());
            reader = new BufferedReader(new FileReader(file1));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            double all=0;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println(tempString);
                String[] splited = tempString.split("\\|",-1);
                double amout = Double.parseDouble(splited[10]);
                int status = Integer.parseInt(splited[19]);
                int gameId = Integer.parseInt(splited[7]);
                String d = splited[6].split(":")[0];
                if(status ==4&& gameId==2948){
                    all = all+amout;
                }


                line++;
            }
            System.out.println(all);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
