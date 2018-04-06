package cn.com.yyft.service.redisimpl;

import cn.com.yyft.service.RedisService;
import cn.com.yyft.utils.PropertiesHelp;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class RegiServiceImpl implements RedisService {
    Logger logger = Logger.getLogger(RegiServiceImpl.class);

    public void store(String message) {
        Jedis jedis = null;
        String game_account = "";
        try {
            //2016-11-16 17:27:40,446 [INFO] bi: bi_regi|2fee010b-d2d1-47||pyw167058695|2368|2016-11-16 17:27:40|6|17||13813285123|6|ANDROID|0|aqy_tjzt_40145|357698064776255&26bd4a96dd2f2175&AC:5A:14:3F:31:A8
            String[] splited = message.split("\\|", -1);

            String requestid = splited[1];//请求流水号
            String userid = splited[2];//通行证id
            game_account = splited[3].toLowerCase().replace(" ", "");//游戏账号
            String game_id = splited[4];//注册游戏id
            String reg_time = splited[5];//操作时间
            String reg_resource = splited[6];//注册来源id 8 6
            String channel_id = splited[7];//渠道id
            String owner_id = splited[8];//归属返利人id：member_id
            String bind_member_id = splited[9];//绑定通行证号id 6手机
            String status = splited[10];//状态
            String reg_os_type = splited[11];//账号所属操作系统
            String expand_code = splited[12];//推广码 8
            String expand_channel = splited[13];//推广渠道
            String imei = splited[14];//imei

            String[] expandCodes = expand_code.split("~", -1);
            String expandCode = expandCodes[0];

            logger.info("game_account: " + game_account);

            jedis = new Jedis(PropertiesHelp.getRelativePathValue("redis.host"),
                    Integer.parseInt(PropertiesHelp.getRelativePathValue("redis.port")));
            jedis.auth("redis");

            if (!"".equals(game_account)) {
                //第一次插入注册账户
                if (!"".equals(reg_resource)) {
                    //获取owner_id=member_id
                    if ("6".equals(reg_resource) && !bind_member_id.equals("")) {
                        owner_id = jedis.get(bind_member_id);
                        bind_member_id = jedis.get(bind_member_id);
                    } else if ("8".equals(reg_resource)) {
                        if (!expandCode.equals("")) {
                            owner_id = jedis.get(expandCode);
                        }
                        if (!bind_member_id.equals("")) {
                            bind_member_id = jedis.get(bind_member_id);
                        }
                    } else if ("2".equals(reg_resource)) {
                        bind_member_id = userid;
                    }


                    Map<String, String> var2 = new HashMap<String, String>();
                    var2.put("requestid", requestid);
                    var2.put("userid", userid);
                    var2.put("game_id", game_id);
                    var2.put("reg_time", reg_time);
                    var2.put("reg_resource", reg_resource);
                    var2.put("channel_id", channel_id);
                    var2.put("owner_id", owner_id == null ? "" : owner_id);
                    var2.put("bind_member_id", bind_member_id == null ? "" : bind_member_id);
                    var2.put("status", status);
                    var2.put("reg_os_type", reg_os_type);

                    var2.put("expand_code", expandCode);
                    String expandCodeChild = "";
                    if (expandCodes.length != 1) {
                        //isEmpty()方法等价于 string.length == 0
                        if (!expandCodes[1].isEmpty()) {
                            expandCodeChild = expandCodes[1];
                        }
                        var2.put("expand_code_child", expandCodeChild);
                    }

                    var2.put("expand_channel", expand_channel);
                    var2.put("imei", imei);

                    jedis.hmset(game_account, var2);
                } else {
                    Map<String, String> var2 = new HashMap<String, String>();
                    if (!"".equals(status)) {
                        var2.put("status", status);
                    }
                    if (!"".equals(owner_id)) {
                        var2.put("owner_id", owner_id);
                    }
                    //var2.put("reg_time", reg_time);
                    jedis.hmset(game_account, var2);
                }
            }
        } catch (Exception e) {
            logger.info("the jedis: " + jedis);
            logger.error("the error: " + e + " - game_account: " + game_account);
            e.printStackTrace();
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

}
