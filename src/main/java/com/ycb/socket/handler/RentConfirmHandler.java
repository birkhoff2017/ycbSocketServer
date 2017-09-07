package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.model.Order;
import com.ycb.socket.service.BatteryService;
import com.ycb.socket.service.OrderService;
import com.ycb.socket.service.StationService;
import com.ycb.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-2.
 */
public class RentConfirmHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            OrderService orderService = NettyServerStart.factory.getBean(OrderService.class);
            StationService stationService = NettyServerStart.factory.getBean(StationService.class);
            BatteryService batteryService = NettyServerStart.factory.getBean(BatteryService.class);
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            Integer orderStatus = 1;
            if ("1".equals(reqMap.get("STATUS"))) {
                //1 借出成功反馈 用户成功取走电池，借出成功。
                orderStatus = 2;
                // 更新设备信息
                stationService.updateStationFromRentconfirm(reqMap);
                // 更新电池信息
                batteryService.updateBatteryFromRentconfirm(reqMap);
            } else if ("3".equals(reqMap.get("STATUS"))) {
                // 3 网络延迟, 借出失败, 超过30s判断延迟。
                orderStatus = 96;
            } else if ("6".equals(reqMap.get("STATUS"))) {
                // 6 没有符合要求的电池, 借出失败 不带电池ID。
                orderStatus = 70;
            } else if ("7".equals(reqMap.get("STATUS"))) {
                // 7 红外异常。
                orderStatus = 69;
            } else if ("8".equals(reqMap.get("STATUS"))) {
                // 8 借出故障 电机故障。
                orderStatus = 68;
            } else if ("9".equals(reqMap.get("STATUS"))) {
                // 9 解锁5V失败
                orderStatus = 67;
            } else if ("30".equals(reqMap.get("STATUS"))) {
                // 30 因同步时间不成功而无法执行借订单。
                orderStatus = 66;
            } else if ("31".equals(reqMap.get("STATUS"))) {
                // 31 因上一个订单未完成而无法执行新订单。
                orderStatus = 65;
            } else if ("32".equals(reqMap.get("STATUS"))) {
                // 32 设备重发3次STATUS=0都没有收到平台应答，通知平台取消该订单。
                orderStatus = 64;
            }
            if (!"0".equals(reqMap.get("STATUS"))) {
                // 更新订单状态
                orderService.updateOrderStatus(reqMap.get("ORDERID"), orderStatus, reqMap.get("STATIONID"), reqMap.get("ID"));
                // 获取借出电池所属订单支付金额
                Order order = orderService.getPaidFromOrder(reqMap.get("ID"));
                // 小程序消息推送
                if ("1".equals(reqMap.get("STATUS"))) {
                    if (null != order) {
                        // 推送租借成功消息
                        orderService.sendRentSuccessMessage(reqMap, order);
                    }
                } else {
                    // 电池弹出失败自动退款至用户余额
                    orderService.refund(order.getCustomerid(), order.getUsefee(), order.getUsefee());
                    // 推送租借失败消息
                    orderService.sendRentFailMessage(reqMap);
                }
            }
            messageRes.setMsg("ERRCODE:0;ERRMSG:none;ORDERID:" + reqMap.get("ORDERID") + ";ACK:" + messageReq.getActValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
