package cn.com.yyft.service.redisimpl;

import cn.com.yyft.service.RedisService;
import cn.com.yyft.utils.PropertiesHelp;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/**
 * Created by JSJSB-0071 on 2017/2/14.
 */
public class OrderServiceImpl implements RedisService {
    Logger logger = Logger.getLogger(OrderServiceImpl.class);


    public void store(String message) {
        //2017-02-14 10:44:43,737 [INFO] root: bi_order ||K1702146N2772915|2|880236|smh3pyw254191164|2017-02-14 10:44:43|2565|21|0|30.00|30.00|30.00|0|2||0.00|0|880236|4|||6||B0193E35604B49B3AECD842DED355715|
        Jedis jedis = null;
        try {
            //logger.info(message);
            jedis = new Jedis(PropertiesHelp.getRelativePathValue("redis.host"),
                    Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.port")));
            jedis.auth("redis");
            String[] splited = message.split("\\|", -1);

            String orderNo = splited[2];//订单号
            String gameAccount = splited[5];//游戏账号
            String gameId = splited[7];//游戏id
            String orderTime = splited[6];//订单时间
            String orderStatus = splited[19];//订单号

            logger.info("order_game_account: " + gameAccount);

            if (!"".equals(gameId) && "4".equals(orderStatus) && !"".equals(orderNo)) {
                jedis.set(gameAccount + "_is_order_no", orderNo + "|" + orderTime);
            }
        } catch (Exception e) {
            logger.info("the jedis: " + jedis);
            logger.error("the error: " + e);
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }
}
