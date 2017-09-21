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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Huo on 2017/9/21.
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
     * @param order 订单
     */
    public void sendBorrowMessage(Order order) {

        //支付宝用户编号
        String openid = this.getUserId(order.getCustomerid());

        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest();

        //顶部色条的色值
        String headColor = "#000000";

        //点击消息后承接页的地址
        String url = "https://www.baidu.com/";
        //底部链接描述文字，如“查看详情”
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
        String keyword2Value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getBorrowTime());

        //keyword3
        String keyword3Color = "#000000";
        String keyword3Value = order.getOrderid();

        //remark
        String remarkColor = "#32cd32";
        String remarkValue = "充电宝自带一根多功能充电线，插头的AB面分别支持苹果/安卓，如插入后没响应，更换另一面即可。";

        request.setBizContent("{" +
                "\"to_user_id\":\"" + openid + "\"," +
                "\"template\":{" +
                "\"template_id\":\"" + GlobalConfig.ALIPAY_SEND_BORROW_MESSAGE + "\"," +
                "\"context\":{" +
                "\"head_color\":\"" + headColor + "\"," +
                "\"url\":\"" + url + "\"," +
                "\"action_name\":\"" + actionName + "\"," +
                "\"keyword1\":{" +
                "\"color\":\"" + keyword1Color + "\"," +
                "\"value\":\"" + keyword1Value + "\"" +
                "        }," +
                "\"keyword2\":{" +
                "\"color\":\"" + keyword2Color + "\"," +
                "\"value\":\"" + keyword2Value + "\"" +
                "        }," +
                "\"keyword3\":{" +
                "\"color\":\"" + keyword3Color + "\"," +
                "\"value\":\"" + keyword3Value + "\"" +
                "        }," +
                "\"first\":{" +
                "\"color\":\"" + firstColor + "\"," +
                "\"value\":\"" + firstValue + "\"" +
                "        }," +
                "\"remark\":{" +
                "\"color\":\"" + remarkColor + "\"," +
                "\"value\":\"" + remarkValue + "\"" +
                "        }" +
                "      }" +
                "    }" +
                "  }");
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            logger.error("通知用户借用成功失败，错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                    "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
        }
    }

    //租借超时时，发送租借失败的通知给用户
    public void sendErrorMessage(Order order) {
        //获取order订单的商户的编号
        String orderid = order.getOrderid();
        //返回给那个用户的支付宝用户编号
        String openid = this.getUserId(order.getCustomerid());
        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest();

        //顶部色条的色值
        String headColor = "#000000";

        //点击消息后承接页的地址
        String url = "https://www.baidu.com/";
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

        request.setBizContent("{" +
                "\"to_user_id\":\"" + openid + "\"," +
                "\"template\":{" +
                "\"template_id\":\"" + GlobalConfig.ALIPAY_SEND_ERROR_MESSAGE + "\"," +
                "\"context\":{" +
                "\"head_color\":\"" + headColor + "\"," +
                "\"url\":\"" + url + "\"," +
                "\"action_name\":\"" + actionName + "\"," +
                "\"keyword1\":{" +
                "\"color\":\"" + keyword1Color + "\"," +
                "\"value\":\"" + orderid + "\"" +
                "        }," +
                "\"first\":{" +
                "\"color\":\"" + firstColor + "\"," +
                "\"value\":\"" + firstValue + "\"" +
                "        }," +
                "\"remark\":{" +
                "\"color\":\"" + remarkColor + "\"," +
                "\"value\":\"" + remarkValue + "\"" +
                "        }" +
                "      }" +
                "    }" +
                "  }");
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            logger.error("通知用户借用成功失败，错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                    "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
        }
    }

    //用户归还成功后，调用发送消息的接口给用户发送归还成功的通知
    private void sendReturnMessage(Order order) {
        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest();

        //返回给那个用户的支付宝用户编号
        String openid = this.getUserId(order.getCustomerid());

        //顶部色条的色值
        String headColor = "#000000";

        //点击消息后承接页的地址
        String url = "https://www.baidu.com/";

        //底部链接描述文字，如“查看详情”
        String actionName = "查看详情";

        //当前文字颜色
        String firstColor = "#000000";
        //模板中占位符的值
        String firstValue = "";
        //归还地点
        //keyword1
        String keyword1Color = "#000000";
        String keyword1Value = order.getAddress();
        //归还时间
        //keyword2
        String keyword2Color = "#000000";
        String keyword2Value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getReturnTime());
        //租用时长
        //keyword3
        String keyword3Color = "#000000";
        //租用的时间，单位  秒
        long l = (order.getReturnTime().getTime() - order.getBorrowTime().getTime()) / 1000;
        //天
        long days = l / 86400;
        //小时
        long hours = l % 86400 / 3600;
        //分钟
        long minutes = l % 86400 % 3600 / 60;
        //秒
        long seconds = l % 86400 % 3600 % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (seconds > 0) {
            sb.append(seconds).append("秒");
        }
        String keyword3Value = sb.toString();
        //订单编号
        //keyword4
        String keyword4Color = "#000000";
        String keyword4Value = order.getOrderid();

        //remark
        String remarkColor = "#32cd32";
        String remarkValue = "此次租借产生费用" + order.getPrice() + "元。如有疑问，请致电4006290808";

        request.setBizContent("{" +
                "\"to_user_id\":\"" + openid + "\"," +
                "\"template\":{" +
                "\"template_id\":\"" + GlobalConfig.ALIPAY_SEND_RETURN_MESSAGE + "\"," +
                "\"context\":{" +
                "\"head_color\":\"" + headColor + "\"," +
                "\"url\":\"" + url + "\"," +
                "\"action_name\":\"" + actionName + "\"," +
                "\"keyword1\":{" +
                "\"color\":\"" + keyword1Color + "\"," +
                "\"value\":\"" + keyword1Value + "\"" +
                "        }," +
                "\"keyword2\":{" +
                "\"color\":\"" + keyword2Color + "\"," +
                "\"value\":\"" + keyword2Value + "\"" +
                "        }," +
                "\"keyword3\":{" +
                "\"color\":\"" + keyword3Color + "\"," +
                "\"value\":\"" + keyword3Value + "\"" +
                "        }," +
                "\"keyword4\":{" +
                "\"color\":\"" + keyword4Color + "\"," +
                "\"value\":\"" + keyword4Value + "\"" +
                "        }," +
                "\"first\":{" +
                "\"color\":\"" + firstColor + "\"," +
                "\"value\":\"" + firstValue + "\"" +
                "        }," +
                "\"remark\":{" +
                "\"color\":\"" + remarkColor + "\"," +
                "\"value\":\"" + remarkValue + "\"" +
                "        }" +
                "      }" +
                "    }" +
                "  }");
        AlipayOpenPublicMessageSingleSendResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            logger.error("通知用户借用成功失败，错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                    "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
        }
    }
}
