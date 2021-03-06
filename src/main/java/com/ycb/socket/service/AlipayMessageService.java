package com.ycb.socket.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayOpenPublicMessageSingleSendRequest;
import com.alipay.api.response.AlipayOpenPublicMessageSingleSendResponse;
import com.ycb.socket.constant.GlobalConfig;
import com.ycb.socket.dao.OrderDao;
import com.ycb.socket.dao.UserDao;
import com.ycb.socket.model.Order;
import com.ycb.socket.utils.JsonUtils;
import com.ycb.socket.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Huo on 2017/9/21.
 * <p>
 * 用于在用户借用电池，归还电池，借出失败时向用户发送通知
 */
@Service
public class AlipayMessageService {

    private static final Logger logger = LoggerFactory.getLogger(AlipayMessageService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * @param customerid 用户编号
     * @return 用户的openid
     */
    private String getUserId(Long customerid) {
        return userDao.getOpenidById(customerid);
    }

    /**
     * 用户借用成功后，调用发送消息的接口给用户发送借用成功的通知
     *
     * @param order 订单
     */
    public void sendBorrowMessage(Order order) {

        //支付宝用户编号
        String openid = this.getUserId(order.getCustomerid());
        //顶部色条的色值
        String headColor = "#000000";
        String session = MD5.getMessageDigest(openid.getBytes());
        //点击消息后承接页的地址为用户的借用历史记录，因此将用户的session带上用于获取到用户的订单记录
        String url = GlobalConfig.ALIPAY_NOTIFY_URL + "/rent.html?session=" + session;
        //底部链接描述文字
        String actionName = "查看详情";
        //当前文字颜色
        String firstColor = "#000000";
        //模板中占位符的值
        String firstValue = "信用借还，免押金借用充电宝";
        //keyword1
        String keyword1Color = "#000000";
        //查询借出商铺的名字
        Map<String, String> map = new LinkedHashMap<>();
        map.put("STATIONID", order.getBorrowStation().toString());
        Map<String, Object> shopInfo = orderDao.getStationShopInfo(map);
        String keyword1Value = (String) shopInfo.get("shopName");
        //keyword2
        String keyword2Color = "#000000";
        String keyword2Value = "";
        try {
            keyword2Value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getBorrowTime());
        } catch (Exception e) {
            keyword2Value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            logger.error("time problem");
        }
        //keyword3
        String keyword3Color = "#000000";
        String keyword3Value = order.getOrderid();
        //remark
        String remarkColor = "#32cd32";
        String remarkValue = "充电宝自带一根多功能充电线，插头的AB面分别支持苹果/安卓，如插入后没响应，更换另一面即可。";
        Map<String, Object> keyword1 = new LinkedHashMap<>();
        keyword1.put("color", keyword1Color);
        keyword1.put("value", keyword1Value);
        Map<String, Object> keyword2 = new LinkedHashMap<>();
        keyword2.put("color", keyword2Color);
        keyword2.put("value", keyword2Value);
        Map<String, Object> keyword3 = new LinkedHashMap<>();
        keyword3.put("color", keyword3Color);
        keyword3.put("value", keyword3Value);
        Map<String, Object> first = new LinkedHashMap<>();
        first.put("color", firstColor);
        first.put("value", firstValue);
        Map<String, Object> remark = new LinkedHashMap<>();
        remark.put("color", remarkColor);
        remark.put("value", remarkValue);
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("head_color", headColor);
        context.put("url", url);
        context.put("action_name", actionName);
        context.put("keyword1", keyword1);
        context.put("keyword2", keyword2);
        context.put("keyword3", keyword3);
        context.put("first", first);
        context.put("remark", remark);
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("template_id", GlobalConfig.ALIPAY_SEND_BORROW_MESSAGE);
        template.put("context", context);
        Map<String, Object> bizContentMap = new LinkedHashMap<>();
        bizContentMap.put("to_user_id", openid);
        bizContentMap.put("template", template);
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest();
            request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
            AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
            response = alipayClient.execute(request);
            logger.info("response" + response.getBody());
        } catch (AlipayApiException e) {
            logger.error("AlipayClient error"+e.getMessage());
        }
        if (response == null || !response.isSuccess()) {
            logger.error("向用户发送借用成功的消息失败");
            if (response != null) {
                logger.error("错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                        "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
            }
        }
    }

    /**
     * 租借超时时，发送租借失败的通知给用户
     *
     * @param order 订单
     */
    public void sendErrorMessage(Order order) {
        //获取order订单的商户的编号
        String orderid = order.getOrderid();
        //返回给那个用户的支付宝用户编号
        String openid = this.getUserId(order.getCustomerid());
        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest();

        //顶部色条的色值
        String headColor = "#000000";

        //点击消息后承接页的地址为附近充电宝站点列表页，带上return_type的目的是
        //进入附近充电宝站点列表页而不是弹出电池借用成功的弹出框
        String url = GlobalConfig.ALIPAY_NOTIFY_URL + "/loading.html?return_type=test";
        //底部链接描述文字，如“查看详情”
        String actionName = "查看详情";

        //当前文字颜色
        String firstColor = "#000000";
        //模板中占位符的值
        String firstValue = "本次租借超时，点击详情重新租借";

        //keyword1
        String keyword1Color = "#000000";

        //remark
        String remarkColor = "#32cd32";
        String remarkValue = "非常抱歉，您此次租借未成功，您可点击详情重新租借。如有疑问，请致电：4006290808";

        Map<String, Object> keyword1 = new LinkedHashMap<>();
        keyword1.put("color", keyword1Color);
        keyword1.put("value", orderid);
        Map<String, Object> first = new LinkedHashMap<>();
        first.put("color", firstColor);
        first.put("value", firstValue);
        Map<String, Object> remark = new LinkedHashMap<>();
        remark.put("color", remarkColor);
        remark.put("value", remarkValue);
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("head_color", headColor);
        context.put("url", url);
        context.put("action_name", actionName);
        context.put("keyword1", keyword1);
        context.put("first", first);
        context.put("remark", remark);
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("template_id", GlobalConfig.ALIPAY_SEND_ERROR_MESSAGE);
        template.put("context", context);
        Map<String, Object> bizContentMap = new LinkedHashMap<>();
        bizContentMap.put("to_user_id", openid);
        bizContentMap.put("template", template);
        request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
        }
        if (response == null || !response.isSuccess()) {
            logger.error("向用户发送借用失败的消息出错");
            if (response != null) {
                logger.error("错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                        "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
            }
        }
    }

    /**
     * 用户归还成功后，调用发送消息的接口给用户发送归还成功的通知
     *
     * @param lastTime  用户借用时长
     * @param useFeeStr 费用
     * @param order     订单
     */
    public void sendReturnMessage(String lastTime, String useFeeStr, Order order) {
        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest();

        //返回给那个用户的支付宝用户编号
        String openid = this.getUserId(order.getCustomerid());

        //顶部色条的色值
        String headColor = "#000000";

        //点击消息后承接页的地址
        String url = GlobalConfig.ALIPAY_NOTIFY_URL + "/oauth/userInfoOauth";

        //底部链接描述文字，如“查看详情”
        String actionName = "查看详情";

        //当前文字颜色
        String firstColor = "#000000";
        //模板中占位符的值
        String firstValue = "你已成功归还云充吧充电宝。";
        //归还地点
        //keyword1
        String keyword1Color = "#000000";
        String keyword1Value = order.getAddress();
        //归还时间
        //keyword2
        String keyword2Color = "#000000";
        // 归还时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String keyword2Value = sdf.format(new Date());

        //租用时长
        //keyword3
        String keyword3Color = "#000000";

        //订单编号
        //keyword4
        String keyword4Color = "#000000";
        String keyword4Value = order.getOrderid();

        //remark
        String remarkColor = "#32cd32";
        String remarkValue = "此次租借产生费用" + useFeeStr + "。如有疑问，请致电400-629-0808";

        Map<String, Object> keyword1 = new LinkedHashMap<>();
        keyword1.put("color", keyword1Color);
        keyword1.put("value", keyword1Value);
        Map<String, Object> keyword2 = new LinkedHashMap<>();
        keyword2.put("color", keyword2Color);
        keyword2.put("value", keyword2Value);
        Map<String, Object> keyword3 = new LinkedHashMap<>();
        keyword3.put("color", keyword3Color);
        keyword3.put("value", lastTime);
        Map<String, Object> keyword4 = new LinkedHashMap<>();
        keyword4.put("color", keyword4Color);
        keyword4.put("value", keyword4Value);
        Map<String, Object> first = new LinkedHashMap<>();
        first.put("color", firstColor);
        first.put("value", firstValue);
        Map<String, Object> remark = new LinkedHashMap<>();
        remark.put("color", remarkColor);
        remark.put("value", remarkValue);
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("head_color", headColor);
        context.put("url", url);
        context.put("action_name", actionName);
        context.put("keyword1", keyword1);
        context.put("keyword2", keyword2);
        context.put("keyword3", keyword3);
        context.put("keyword4", keyword4);
        context.put("first", first);
        context.put("remark", remark);
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("template_id", GlobalConfig.ALIPAY_SEND_RETURN_MESSAGE);
        template.put("context", context);
        Map<String, Object> bizContentMap = new LinkedHashMap<>();
        bizContentMap.put("to_user_id", openid);
        bizContentMap.put("template", template);
        request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
        }
        if (response == null || !response.isSuccess()) {
            logger.error("向用户发送归还成功的消息失败");
            if (response != null) {
                logger.error("错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                        "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
            }
        }
    }
}
