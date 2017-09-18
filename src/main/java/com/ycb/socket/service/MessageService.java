package com.ycb.socket.service;

import com.ycb.socket.constant.GlobalConfig;
import com.ycb.socket.dao.UserDao;
import com.ycb.socket.model.Message;
import com.ycb.socket.model.WechatTemplateMsg;
import com.ycb.socket.utils.HttpRequest;
import com.ycb.socket.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by duxinyuan on 17-8-29.
 */
@Service
public class MessageService {

    public static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserDao userDao;

    public String getAccessToken() throws Exception {
        String ACCESS_TOKEN = redisService.getRedis().get("ACCESS_TOKEN");
        if (StringUtils.isEmpty(ACCESS_TOKEN)) {
            String param = "grant_type=client_credential&appid=" + GlobalConfig.APP_ID + "&secret=" + GlobalConfig.APP_SECRET;
            try {
                String tokenInfo = HttpRequest.sendGet(GlobalConfig.WX_ACCESS_TOKEN_URL, param);
                Map<String, Object> tokenInfoMap = JsonUtils.readValue(tokenInfo);
                String accessToken = tokenInfoMap.get("access_token").toString();
                Integer expiresIn = Integer.valueOf(tokenInfoMap.get("expires_in").toString());
                // 将accessToken存入Redis,存放时间为7200秒
                redisService.getRedis().set("ACCESS_TOKEN", expiresIn, accessToken, 300);
                return accessToken.trim();
            } catch (Exception e) {
                logger.error(e.getMessage());
                return null;
            }
        } else {
            return ACCESS_TOKEN.trim();
        }
    }

    /**
     * @param customerid
     * @return
     */
    public Message getFormIdByOpenid(Long customerid) {
        String openid = userDao.getOpenidById(customerid);
        return userDao.getUserLastMessage(openid);
    }

    /**
     * 归还成功 推送消息
     *
     * @param openid
     * @param formid
     * @param returnTime
     * @param address
     * @param lastTime
     * @param useFeeStr
     * @param orderid
     */
    public void returnBackSendTMessage(String openid, String formid, String returnTime, String address, String lastTime, String useFeeStr, String orderid) {
        TreeMap<String, TreeMap<String, String>> params = new TreeMap<String, TreeMap<String, String>>();
        //根据具体模板参数组装
        WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
        wechatTemplateMsg.setTemplate_id(GlobalConfig.TEMPLATE_RENTURN_BACK_SUCCESS);
        wechatTemplateMsg.setTouser(openid);
        wechatTemplateMsg.setPage("/pages/user/user");
        wechatTemplateMsg.setForm_id(formid);
        params.put("keyword1", WechatTemplateMsg.item(returnTime, "#000000"));//归还时间
        params.put("keyword2", WechatTemplateMsg.item(address, "#000000"));//归还地点
        params.put("keyword3", WechatTemplateMsg.item(lastTime, "#000000"));//租借时长
        params.put("keyword4", WechatTemplateMsg.item(useFeeStr, "#000000"));//产生费用
        params.put("keyword5", WechatTemplateMsg.item(orderid, "#000000"));//订单号
        params.put("keyword6", WechatTemplateMsg.item("", "#000000"));//备注
        wechatTemplateMsg.setData(params);
        String data = JsonUtils.writeValueAsString(wechatTemplateMsg);
        //发送请求
        try {
            String token = this.getAccessToken();
            String msgUrl = GlobalConfig.WX_SEND_TEMPLATE_MESSAGE + "?access_token=" + token;
            String msgResult = HttpRequest.sendPost(msgUrl, data);  //发送post请求
            Map<String, Object> msgResultMap = JsonUtils.readValue(msgResult);
            Integer errcode = (Integer) msgResultMap.get("errcode");
            String errmsg = (String) msgResultMap.get("errmsg");
            if (0 == errcode) {
                //result = true;
                logger.info("模板消息发送成功errorCode:{" + errcode + "},errmsg:{" + errmsg + "}");
            } else {
                logger.info("模板消息发送失败errorCode:{" + errcode + "},errmsg:{" + errmsg + "}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMessageById(Long messageid) {
        userDao.deleteMessageById(messageid);
    }

    public void updateMessageNumberById(Long messageid) {
        userDao.updateMessageNumberById(messageid);
    }

    /**
     * 租借成功 推送消息
     *
     * @param openid
     * @param formid
     * @param borrowTime
     * @param address
     * @param freeTime
     * @param deposit
     * @param orderid
     */
    public void rentSuccessSendTMessage(String openid, String formid, String borrowTime, String address,
                                        String freeTime, String deposit, String orderid, String feeStrategy) {
        TreeMap<String, TreeMap<String, String>> params = new TreeMap<String, TreeMap<String, String>>();
        WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
        wechatTemplateMsg.setTemplate_id(GlobalConfig.TEMPLATE_RENT_SUCCESS);
        wechatTemplateMsg.setTouser(openid);
        wechatTemplateMsg.setPage("/pages/user/user");
        wechatTemplateMsg.setForm_id(formid);
        params.put("keyword1", WechatTemplateMsg.item(borrowTime, "#000000"));//租借时间
        params.put("keyword2", WechatTemplateMsg.item(address, "#000000"));//租借地点
        params.put("keyword3", WechatTemplateMsg.item(freeTime, "#000000"));//免费使用时长
        params.put("keyword4", WechatTemplateMsg.item(deposit, "#000000"));//支付押金
        params.put("keyword5", WechatTemplateMsg.item(orderid, "#000000"));//订单号
        params.put("keyword6", WechatTemplateMsg.item(feeStrategy, "#000000"));//租赁价格
        wechatTemplateMsg.setData(params);
        String data = JsonUtils.writeValueAsString(wechatTemplateMsg);
        //发送请求
        try {
            String token = this.getAccessToken();
            String msgUrl = GlobalConfig.WX_SEND_TEMPLATE_MESSAGE + "?access_token=" + token;
            String msgResult = HttpRequest.sendPost(msgUrl, data);  //发送post请求
            Map<String, Object> msgResultMap = JsonUtils.readValue(msgResult);
            Integer errcode = (Integer) msgResultMap.get("errcode");
            String errmsg = (String) msgResultMap.get("errmsg");
            if (0 == errcode) {
                //result = true;
                logger.info("模板消息发送成功errorCode:{" + errcode + "},errmsg:{" + errmsg + "}");
            } else {
                logger.info("模板消息发送失败errorCode:{" + errcode + "},errmsg:{" + errmsg + "}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
