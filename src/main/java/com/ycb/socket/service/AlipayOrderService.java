package com.ycb.socket.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.ZhimaMerchantOrderRentCancelRequest;
import com.alipay.api.response.ZhimaMerchantOrderRentCancelResponse;
import com.ycb.socket.constant.GlobalConfig;
import com.ycb.socket.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        //信用借还的产品码
        String productCode = GlobalConfig.ALIPAY_PRODUCT_CODE;

        Map<String, Object> bizContentMap = new LinkedHashMap<>();
        bizContentMap.put("order_no", orderNo);
        bizContentMap.put("product_code", productCode);
        request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
        ZhimaMerchantOrderRentCancelResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            //取消订单成功，不做任何处理
        } else {
            logger.error("信用借还订单取消失败，错误代码：" + response.getCode() + "错误信息：" + response.getMsg() +
                    "错误子代码" + response.getSubCode() + "错误子信息：" + response.getSubMsg());
        }
    }

//    /**
//     * 在用户归还电池后，调用支付宝的完结订单接口完结订单
//     *
//     * @param order 订单
//     */
//    public void CompleteOrder(Order order) {
//        AlipayClient alipayClient = new DefaultAlipayClient(GlobalConfig.ALIPAY_SERVER_URL, GlobalConfig.ALIPAY_APPID, GlobalConfig.ALIPAY_PRIVATEKEY, GlobalConfig.ALIPAY_FORMAT, GlobalConfig.ALIPAY_CHARSET, GlobalConfig.ALIPAY_ALIPAYPUBLICKEY, GlobalConfig.ALIPAY_SIGNTYPE);
//        ZhimaMerchantOrderRentCompleteRequest request = new ZhimaMerchantOrderRentCompleteRequest();
//        //根据orderID获得信用借还订单的支付宝的编号
//        Order order = orderMapper.findOrderByOrderId(orderid);
//        String orderNo = order.getOrderNo();
//        //信用借还的产品码:w1010100000000002858
//        String productCode = GlobalConfig.Z_PRODUCT_CODE;
//        //物品归还时间	2016-10-01 12:00:00
//        String restoreTime = new SimpleDateFormat("YYYY-MM-dd HH:MM:ss").format(order.getReturnTime());
//        /*
//        金额类型：
//        RENT:租金
//        DAMAGE:赔偿金
//         */
//        String payAmountType = "RENT";
//        //支付金额	100.00
//        //payAmount 需要支付的金额
//        String payAmount = order.getUsefee().toString();
//        //restoreShopName 物品归还门店名称,例如肯德基文三路门店
//        Long returnShopId = order.getReturnShopId();
//        Shop shopInfo = shopMapper.findShopById(returnShopId);
//        String restoreShopName = shopInfo.getName();
//
//        Map<String, Object> bizContentMap = new LinkedHashMap<>();
//        bizContentMap.put("order_no", orderNo);
//        bizContentMap.put("product_code", productCode);
//        bizContentMap.put("restore_time", restoreTime);
//        bizContentMap.put("pay_amount_type", payAmountType);
//        bizContentMap.put("pay_amount", payAmount);
//        bizContentMap.put("restore_shop_name", restoreShopName);
//
//        request.setBizContent(JsonUtils.writeValueAsString(bizContentMap));
//        ZhimaMerchantOrderRentCompleteResponse response = null;
//        try {
//            response = alipayClient.execute(request);
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
//        if (response.isSuccess()) {
//            System.out.println("调用成功,信用借还订单完结");
//            //更新订单信息
//            updateOrder(response);
//            //通知用户归还成功
////            sendMessage(response.getUserId(), order, restoreShopName);
//        } else {
//            System.out.println("调用失败");
//        }
//    }

}
