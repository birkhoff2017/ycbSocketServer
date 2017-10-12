package com.ycb.socket.service;

import com.ycb.socket.dao.OrderDao;
import com.ycb.socket.model.FeeStrategy;
import com.ycb.socket.model.Message;
import com.ycb.socket.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-16.
 */
@Service
public class OrderService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private FeeStrategyService feeStrategyService;

    @Autowired
    private OrderDao orderDao;

    public void updateOrderStatus(String orderid, Integer orderStatus, String stationid, String batteryid) {
        orderDao.updateOrderStatus(orderid, orderStatus, stationid, batteryid);
    }

    public void updateOrderFromRetrunback(Map<String, String> reqMap, BigDecimal usefee) {
        orderDao.updateOrderFromRetrunback(reqMap, usefee);
    }

    /**
     * 推送租借成功消息
     *
     * @param reqMap
     * @param order
     */
    public void sendRentSuccessMessage(Map<String, String> reqMap, Order order) {
        // 租借时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String borrowTime = sdf.format(order.getBorrowTime());
        // 租借地点
        Map<String, Object> shopInfo = orderDao.getStationShopInfo(reqMap);
        String address = (String) shopInfo.get("shopName");
        // 订单号
        String orderid = order.getOrderid();
        Message formMessage = this.messageService.getFormIdByOpenid(order.getCustomerid());
        if (null != formMessage) {
            FeeStrategy feeEntity = feeStrategyService.getFeeStrategy(order.getFeeSettings());
            String feeStrategy = feeStrategyService.transFeeStrategy(feeEntity);
            String freeTime = feeStrategyService.getFreeTime(feeEntity);
            String deposit = order.getPaid().toString();
            //使用prepay_id推送消息
            messageService.rentSuccessSendTMessage(formMessage.getOpenid(), formMessage.getFormId(), borrowTime, address,
                    freeTime, deposit, orderid, feeStrategy);
            //减掉prepay_id的使用次数，如果为0 直接删除
            if (1 >= formMessage.getNumber()) {
                this.messageService.deleteMessageById(formMessage.getId());
            } else {
                this.messageService.updateMessageNumberById(formMessage.getId());
            }
        } else {
            logger.info("orderId:" + order.getOrderid() + "该条退款消息推送失败！没有可用的form_id了");
        }
    }

    /**
     * 电池弹出失败自动退款至用户余额
     *
     * @param customerid
     */
    public void refund(Long customerid, BigDecimal deposit, BigDecimal usablemoney) {
        // 根据订单支付金额，将用户押金退还至余额
        userService.updateUserFee(customerid, deposit, usablemoney);
    }

    /**
     * 推送租借失败消息
     *
     * @param reqMap
     */
    public void sendRentFailMessage(Map<String, String> reqMap) {
    }


    /**
     * 推送归还成功消息
     *
     * @param lastTime
     * @param useFeeStr
     * @param order
     */
    public void sendReturnSuccessMessage(String lastTime, String useFeeStr, Order order) {
        // 归还时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String returnTime = sdf.format(new Date());
        // 归还地点
        String address = order.getAddress();
        // 订单号
        String orderid = order.getOrderid();
        Message formMessage = this.messageService.getFormIdByOpenid(order.getCustomerid()); //获取prepay_id
        if (null != formMessage) {
            //使用prepay_id推送消息
            messageService.returnBackSendTMessage(formMessage.getOpenid(), formMessage.getFormId(), returnTime, address, lastTime, useFeeStr, orderid);
            //减掉prepay_id的使用次数，如果为0 直接删除
            if (1 >= formMessage.getNumber()) {
                this.messageService.deleteMessageById(formMessage.getId());
            } else {
                this.messageService.updateMessageNumberById(formMessage.getId());
            }
        } else {
            logger.info("orderId:" + order.getOrderid() + "该条退款消息推送失败！没有可用的form_id了");
        }
    }

    /**
     * 获取借出电池所属订单详情
     *
     * @param id
     * @return
     */
    public Order getPaidFromOrder(String id) {
        return orderDao.getPaidOrder(id);
    }

    /**
     * 获取归还电池所属订单详情
     *
     * @param batteryid
     * @return
     */
    public Order getBackBatteryOrder(String batteryid) {
        return orderDao.getBackBatteryOrder(batteryid);
    }

    public Order getUpdatedOrderByOrderId(String orderid) {
        return orderDao.getUpdatedOrderByOrderId(orderid);
    }
}
