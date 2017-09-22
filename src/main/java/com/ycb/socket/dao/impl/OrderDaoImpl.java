package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.OrderDao;
import com.ycb.socket.model.Order;
import com.zipeiyi.xpower.dao.DaoFactory;
import com.zipeiyi.xpower.dao.IDao;
import com.zipeiyi.xpower.dao.OpResult;
import com.zipeiyi.xpower.dao.OpUpdate;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-16.
 */
@Repository
public class OrderDaoImpl implements OrderDao {
    final IDao dao = DaoFactory.getIDao();
    final String bizName = "ycb";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void updateOrderStatus(String orderid, Integer orderStatus, String stationid, String batteryid) {
        StringBuffer sql = new StringBuffer();
        if (orderStatus == 2) {
            // 借出成功更新借出时间
            sql.append("UPDATE ycb_mcs_tradelog ")
                    .append("SET optlock = optlock + 1, ")
                    .append("lastModifiedBy = 'SYS:station', ")
                    .append("lastModifiedDate = NOW(), ")
                    .append("borrow_time = NOW(), ")
                    .append("borrow_station_id = ?, ")
                    .append("borrow_battery_rfid = ?, ")
                    .append("status = ? ")
                    .append("WHERE orderid = ? ");
        } else {
            sql.append("UPDATE ycb_mcs_tradelog ")
                    .append("SET optlock = optlock + 1, ")
                    .append("lastModifiedBy = 'SYS:station', ")
                    .append("lastModifiedDate = NOW(), ")
                    .append("borrow_station_id = ?, ")
                    .append("borrow_battery_rfid = ?, ")
                    .append("status = ? ")
                    .append("WHERE orderid = ? ");
        }
        dao.update(new OpUpdate(sql, bizName).addParams(stationid, batteryid, orderStatus, orderid));
    }

    @Override
    public Order getPaidOrder(String id) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT ")
                .append("t.orderid, ")
                .append("t.customer, ")
                .append("t.paid, ")
                .append("t.borrow_time, ")
                .append("t.return_time, ")
                .append("t.usefee, ")
                .append("t.borrow_station_name address, ")
                .append("t.platform, ")
                .append("t.order_no, ")
                .append("t.alipay_fund_order_no, ")
                .append("ss.fee_settings ")
                .append("FROM ycb_mcs_tradelog t ")
                .append("LEFT JOIN ycb_mcs_shop_station ss ")
                .append("ON t.borrow_shop_station_id = ss.id ")
                .append("WHERE t.borrow_battery_rfid = ? ")
                .append("AND t.status = 2 ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            Order order = null;
            while (rs.next()) {
                order = new Order();
                order.setOrderid(rs.getString("orderid"));
                order.setCustomerid(rs.getLong("customer"));
                order.setPaid(rs.getBigDecimal("paid"));
                order.setBorrowTime(rs.getTimestamp("borrow_time"));
                order.setReturnTime(rs.getTimestamp("return_time"));
                order.setFeeSettings(rs.getLong("fee_settings"));
                order.setAddress(rs.getString("address"));
                order.setUsefee(rs.getBigDecimal("usefee"));
                order.setPlatform(rs.getInt("platform"));
                order.setOrderNo(rs.getString("order_no"));
                order.setAlipayFundOrderNo(rs.getString("alipay_fund_order_no"));
            }
            return order;
        }).addParams(id));
    }

    @Override
    public void updateOrderFromRetrunback(Map<String, String> reqMap, BigDecimal usefee) {
        Map<String, Object> map = getStationShopInfo(reqMap);
        if (!map.isEmpty()) {
            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE ycb_mcs_tradelog ")
                    .append("SET optlock = optlock + 1, ")
                    .append("lastModifiedBy = 'SYS:returnback', ")
                    .append("lastModifiedDate = NOW(), ")
                    .append("status = 3, ")//归还
                    .append("usefee = ?, ")//花费计算
                    .append("return_time = ?, ")
                    .append("return_shop_id = ?, ")
                    .append("return_shop_station_id = ?, ")
                    .append("return_station_id = ?, ")
                    .append("return_station_name = ? ")
                    .append("WHERE borrow_battery_rfid = ? ")
                    .append("AND status = 2 ");
            dao.update(new OpUpdate(sql, bizName).addParams(
                    usefee,
                    reqMap.get("TIME") == null ? new Date() : new Timestamp(Long.valueOf(reqMap.get("TIME")) * 1000),
                    map.get("shopid"),
                    map.get("ssid"),
                    reqMap.get("STATIONID"),
                    map.get("shopName"),
                    reqMap.get("ID")
            ));
        }
    }

    @Override
    public Map<String, Object> getStationShopInfo(Map<String, String> reqMap) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT ")
                .append("ss.id AS ssid, ")//归还商铺站点id
                .append("shop.id AS shopid, ")//归还商铺id
                .append("shop.name AS shopName ")//归还商铺名称
                .append("FROM ycb_mcs_shop shop, ycb_mcs_shop_station ss, ycb_mcs_station s ")
                .append("WHERE ss.shopid = shop.id ")
                .append("AND ss.station_id = s.sid ")
                .append("AND ss.status = 1 ") // 商铺和设备绑定
                .append("AND s.sid = ? ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            Map<String, Object> ret = new HashedMap();
            while (rs.next()) {
                ret.put("shopid", rs.getLong("shopid"));
                ret.put("ssid", rs.getLong("ssid"));
                ret.put("shopName", rs.getString("shopName"));
            }
            return ret;
        }).addParams(reqMap.get("STATIONID")));
    }

    @Override
    public Order getBackBatteryOrder(String batteryid) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT ")
                .append("t.orderid, ")
                .append("t.customer, ")
                .append("t.paid, ")
                .append("t.borrow_time, ")
                .append("t.return_time, ")
                .append("t.usefee, ")
                .append("t.return_station_name address, ")
                .append("ssb.fee_settings ")
                .append("FROM ycb_mcs_tradelog t ")
                .append("LEFT JOIN ycb_mcs_shop_station ssb ")
                .append("ON t.borrow_shop_station_id = ssb.id ")
                .append("WHERE t.borrow_battery_rfid = ? ")
                .append("AND t.status = 3 ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            Order order = null;
            while (rs.next()) {
                order = new Order();
                order.setOrderid(rs.getString("orderid"));
                order.setCustomerid(rs.getLong("customer"));
                order.setPaid(rs.getBigDecimal("paid"));
                order.setBorrowTime(rs.getDate("borrow_time"));
                order.setReturnTime(rs.getDate("return_time"));
                order.setFeeSettings(rs.getLong("fee_settings"));
                order.setAddress(rs.getString("address"));
                order.setUsefee(rs.getBigDecimal("usefee"));
            }
            return order;
        }).addParams(batteryid));
    }
}