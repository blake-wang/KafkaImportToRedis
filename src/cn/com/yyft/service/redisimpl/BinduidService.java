package cn.com.yyft.service.redisimpl;

import cn.com.yyft.service.RedisService;
import cn.com.yyft.utils.JedisUtils;
import cn.com.yyft.utils.PropertiesHelp;
import cn.com.yyft.utils.RedisUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sumenghu on 2016/11/9.
 */
public class BinduidService implements RedisService {
    Logger logger = Logger.getLogger(BinduidService.class);

    @Override
    public void store(String message) {
        Jedis jedis = null;
        try {
            //2017-01-03 20:26:12,561 [INFO] root: bi_binduid |8258c|16524|65|17|2017-01-03 20:26:12|1
            jedis = new Jedis(PropertiesHelp.getRelativePathValue("redis.host"),
                    Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.port")));
            jedis.auth("redis");
            String[] splited = message.split("\\|", -1);

            String game_account = splited[1].toLowerCase().replace(" ", "");//请求流水号
            if (!"".equals(game_account)) {
                String userid = splited[2].trim();//通行证id
                String bindStatus = splited[6];//绑定状态
                logger.info("BinduidService:game_account: " + game_account + "-" + userid);
                if (!"1".equals(bindStatus)) {
                    userid = "0";
                }

                //若给的userid为手机号 需要从redis中找
                if (userid.length() >= 10) {
                    String uid = jedis.get(userid);
                    if (uid == null || uid.equals("")) {
                        userid = "0";

                    } else {
                        userid = uid;
                    }
                }

                Map<String, String> var2 = new HashMap<String, String>();
                var2.put("userid", userid);
                var2.put("bind_member_id", userid);
                jedis.hmset(game_account, var2);
            }

        } catch (Exception e) {
            logger.info("the jedis: " + jedis);
            logger.error("the error: " + e);
            e.printStackTrace();
        } finally {
            if (jedis !=null)
            jedis.close();
        }
    }

}
