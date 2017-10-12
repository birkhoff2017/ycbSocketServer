package com.ycb.socket.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.ZhimaMerchantOrderRentCancelRequest;
import com.alipay.api.request.ZhimaMerchantOrderRentCompleteRequest;
import com.alipay.api.response.ZhimaMerchantOrderRentCancelResponse;
import com.alipay.api.response.ZhimaMerchantOrderRentCompleteResponse;
import com.ycb.socket.constant.GlobalConfig;
import com.ycb.socket.model.Order;
import com.ycb.socket.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Huo on 2017/9/22.
 */
@Service
public class AlipayOrderService {

    private final Logger logger = LoggerFactory.getLogger(AlipayOrderService.class);

    /**
     * 在电池弹出失败时调用支付宝的取消订单接口，取消支付宝已经生成的信用借还订单
     *
     * @param orderNo 信用借还订单号
     */
    public void cancelOrder(String orderNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        ZhimaMerchantOrderRentCancelRequest request = new ZhimaMerchantOrderRentCancelRequest();

        Map<String, Object> bizContentMap = new LinkedHashMap<>();
        bizContentMap.put("order_no", orderNo);
        bizContentMap.put("product_code", GlobalConfig.ALIPAY_PRODUCT_CODE);
        request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
        ZhimaMerchantOrderRentCancelResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
        }
        if (response == null || !response.isSuccess()) {
            logger.error("信用借还订单取消失败" + orderNo);
            if (response != null) {
                logger.error("错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                        "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
            }
        }
    }

    /**
     * 在用户归还电池后，调用支付宝的完结订单接口完结订单
     *
     * @param order 订单
     * @param usefee
     */
    public boolean completeOrder(Order order, BigDecimal usefee) {
        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
        ZhimaMerchantOrderRentCompleteRequest request = new ZhimaMerchantOrderRentCompleteRequest();
        //获得信用借还订单支付宝的订单编号
        String orderNo = order.getOrderNo();
        //物品归还时间
        String restoreTime = "";
        try {
            restoreTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getReturnTime());
        }catch (Exception e){
            restoreTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        /*
        金额类型：
        RENT:租金
         */
        String payAmountType = "RENT";
        //payAmount 需要支付的金额
        String payAmount = usefee.toString();
        //restoreShopName 物品归还门店名称
        String restoreShopName = order.getAddress();

        Map<String, Object> bizContentMap = new LinkedHashMap<>();
        bizContentMap.put("order_no", orderNo);
        bizContentMap.put("product_code", GlobalConfig.ALIPAY_PRODUCT_CODE);
        bizContentMap.put("restore_time", restoreTime);
        bizContentMap.put("pay_amount_type", payAmountType);
        bizContentMap.put("pay_amount", payAmount);
        bizContentMap.put("restore_shop_name", restoreShopName);

        request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
        ZhimaMerchantOrderRentCompleteResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            logger.error(e.getMessage());
        }
        if (response == null || !response.isSuccess()) {
            logger.error("信用借还订单完结失败" + orderNo);
            if (response != null) {
                logger.error("错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                        "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
            }
            return false;
        }
        return true;
    }
}
