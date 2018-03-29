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
 * Created by Sumenghu on 2016/11/8.
 */
public class MemberServiceImpl implements RedisService {
    Logger logger = Logger.getLogger(MemberServiceImpl.class);

    public void store(String message) {
        //2016-11-16 17:26:31,556 [INFO] root: bi_member |205715|13813285123||||||6|0|||||2016-11-16 17:26:31|1
        Jedis jedis = null;
        try {

            jedis =  new Jedis(PropertiesHelp.getRelativePathValue("redis.host"),
                    Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.port")));
            jedis.auth("redis");
            String[] splited = message.split("\\|", -1);
            String memberId = splited[1];
            logger.info("memberId: " + memberId);
            String userName = splited[2];//手机号
            String tel = splited[3];
            String qq = splited[4];
            String sex = splited[5];
            String descr = splited[6];
            String email = splited[7];
            String regtype = splited[8];
            String grade = splited[9];
            String vip_note = splited[10];
            String promo_code = splited[11];//推广码
            String promo_member_id = splited[12];
            String invite_code = splited[13];
            String regtime = splited[14];
            String status = splited[15];

            //status=1 插入Member
            if ("1".equals(status)) {
                Map<String, String> members = new HashMap<String, String>();
                members.put("username", userName);
                members.put("tel", tel);
                members.put("qq", qq);
                members.put("sex", sex);
                members.put("descr", descr);
                members.put("email", email);
                members.put("regtype", regtype);
                members.put("grade", grade);
                members.put("vip_note", vip_note);
                members.put("promo_code", promo_code);
                members.put("promo_member_id", promo_member_id);
                members.put("invite_code", invite_code);
                members.put("regtime", regtime);
                members.put("status", status);

                jedis.hmset(memberId + "_member", members);

                if (!"".equals(userName)) {
                    jedis.set(userName, memberId);
                }
            }

            //status 2,仅更新invite_code,status
            if("2".equals(status)){
                Map<String, String> members = new HashMap<String, String>();
                members.put("invite_code", invite_code);
                if (!"".equals(invite_code)) {
                    jedis.set(invite_code, memberId);
                }
                members.put("status", status);
                jedis.hmset(memberId + "_member", members);
            }
            //status 3,status
            if("3".equals(status)){
                Map<String, String> members = new HashMap<String, String>();
                members.put("status", status);
                jedis.hmset(memberId + "_member", members);
            }

        } catch (Exception e) {
            logger.info("the jedis: "+jedis);
            logger.error("the error: " + e);
            e.printStackTrace();
        } finally {
            if (jedis !=null)
            jedis.close();
        }
    }


}
