package com.ycb.socket.dao;

import com.ycb.socket.model.Order;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-16.
 */
public interface OrderDao {
    void updateOrderStatus(String orderid, Integer orderStatus, String stationid, String batteryid);

    void updateOrderFromRetrunback(Map<String, String> reqMap, BigDecimal usefee);

    Order getPaidOrder(String id);

    Order getBackBatteryOrder(String batteryid);

    Map<String, Object> getStationShopInfo(Map<String, String> reqMap);

    Order getUpdatedOrderByOrderId(String orderid);
}
