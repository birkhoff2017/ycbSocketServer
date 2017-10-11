package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.model.FeeStrategy;
import com.ycb.socket.model.Order;
import com.ycb.socket.service.*;
import com.ycb.socket.utils.StringUtils;
import com.ycb.socket.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-2.
 */
public class ReturnBackHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            BatteryService batteryService = NettyServerStart.factory.getBean(BatteryService.class);
            OrderService orderService = NettyServerStart.factory.getBean(OrderService.class);
            UserService userService = NettyServerStart.factory.getBean(UserService.class);
            FeeStrategyService feeStrategyService = NettyServerStart.factory.getBean(FeeStrategyService.class);
            AlipayMessageService alipayMessageService = NettyServerStart.factory.getBean(AlipayMessageService.class);
            AlipayOrderService alipayOrderService = NettyServerStart.factory.getBean(AlipayOrderService.class);
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            // 根据电池ID检索电池表，新建数据
            Boolean exist = batteryService.findByBatteryId(reqMap.get("ID"));
            if (exist) {
                // 租借时长
                String lastTime = "";
                // 产生费用
                String useFeeStr = "0";
                Long duration = 0l;
                // 获取借出电池所属订单详情
                Order borrowOrder = orderService.getPaidFromOrder(reqMap.get("ID"));
                if (borrowOrder != null) {
                    //获取到订单所属平台
                    Integer platform = borrowOrder.getPlatform();
                    // 计算订单使用金额
                    if (reqMap.get("TIME") == null) {
                        duration = (new Date().getTime() - borrowOrder.getBorrowTime().getTime()) / 1000;
                    } else {
                        duration = Long.valueOf(reqMap.get("TIME")) - borrowOrder.getBorrowTime().getTime() / 1000;
                    }
                    FeeStrategy feeStrategy = feeStrategyService.getFeeStrategy(borrowOrder.getFeeSettings());
                    BigDecimal usefee = feeStrategyService.calUseFee(feeStrategy, duration);
                    //只有当不是支付宝平台的订单的时候才执行该业务逻辑
                    if (platform != 2) {
                        BigDecimal refund = borrowOrder.getPaid().subtract(usefee);
                        if (borrowOrder.getPaid().compareTo(usefee) < 0) {
                            usefee = borrowOrder.getPaid();
                            refund = BigDecimal.ZERO;
                        }
                        // 更新用户账户信息(归还成功，订单押金退还到余额)
                        userService.updateUserFee(borrowOrder.getCustomerid(), borrowOrder.getPaid(), refund);
                    }
                    lastTime = TimeUtil.timeToString(duration);
                    useFeeStr = usefee + "元";

                    // 更新订单信息 订单状态由借出->归还
                    orderService.updateOrderFromRetrunback(reqMap, usefee);
                    //查询更新后的订单表
                    Order backBatteryOrder = orderService.getBackBatteryOrder(reqMap.get("ID"));
                    //判断是哪个平台的订单就调用哪个平台的消息推送service，2为支付宝的订单，3为小程序的订单
                    if (3 == platform) {
                        // 推送归还成功消息
                        orderService.sendReturnSuccessMessage(lastTime, useFeeStr, backBatteryOrder);
                    } else if (2 == platform) {
                        //调用支付宝完结订单的接口完结订单
                        alipayOrderService.completeOrder(borrowOrder);
                        // 推送归还成功消息
                        alipayMessageService.sendReturnMessage(lastTime, useFeeStr, backBatteryOrder);
                    }
                }
                // 更新电池表信息
                batteryService.updateBatteryInfo(reqMap);
            } else {
                // 新增电池
                batteryService.insertBatteryInfo(reqMap);
            }
            //电池归还指令值没有空槽数在机数，不能更新设备信息，待同步时去更新
            //stationService.updateStationBatteryInfo(reqMap);
            messageRes.setMsg("ERRCODE:0;ERRMSG:none" + ";ID:" + reqMap.get("ID") + ";ACK:" + messageReq.getActValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
