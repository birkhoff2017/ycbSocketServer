package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.BatteryDao;
import com.zipeiyi.xpower.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-11.
 */
@Repository
public class BatteryDaoImpl implements BatteryDao {
    final IDao dao = DaoFactory.getIDao();
    final String bizName = "ycb";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Integer findByBatteryId(String id) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(1) count FROM ycb_mcs_battery ")
                .append("WHERE ")
                .append("rfid = ? ");
        return dao.queryUniq(new DefaultOpUniq<Integer>(sql, bizName).setMapper((resultSet, i) -> resultSet.getInt("count")).addParams(id));
    }

    @Override
    public void updateBatteryInfo(Map<String, String> reqMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_battery ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("batt_type = ?, ")
                .append("battery_key = ?, ")
                .append("last_back_time = ?, ")
                .append("stationid = ?, ")
                .append("slot = ?, ")
                .append("dev_info = ? ")
                .append("WHERE rfid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("BATT_TYPE"),
                Boolean.parseBoolean(reqMap.get("KEY")),
                new Timestamp(Long.valueOf(reqMap.get("TIME")) * 1000),// 归还时间
                reqMap.get("STATIONID"),
                reqMap.get("SLOT"),
                reqMap.get("DEV_INFO"),
                reqMap.get("ID")
        ));
    }

    @Override
    public void insertBatteryInfo(Map<String, String> reqMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ycb_mcs_battery(createdBy,createdDate,optlock,rfid,battery_key,last_back_time,stationid,slot,dev_info,batt_type) ")
                .append("VALUES('SYS:station', NOW(), 0, ?, ?, ?, ?, ?, ?, ?)");
        dao.insert(new OpInsert<>(sql, bizName, Long.class)
                .addParams(
                        reqMap.get("ID"),
                        Boolean.parseBoolean(reqMap.get("KEY")),
                        new Timestamp(Long.valueOf(reqMap.get("TIME")) * 1000),
                        reqMap.get("STATIONID"),
                        reqMap.get("SLOT"),
                        reqMap.get("DEV_INFO"),
                        reqMap.get("BATT_TYPE")
                ));
    }

    @Override
    public void syncBatteryInfo(String slot, Map<String, String> reqMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_battery ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("sync_time = NOW(), ")
                .append("batt_type = ?, ")
                .append("battery_key = ?, ")
                //.append("last_back_time = ?, ")
                .append("stationid = ?, ")
                .append("slot = ?, ")
                .append("power = ?, ")
                .append("isdamage = ?, ")
                .append("voltage = ?, ")
                .append("currentval = ?, ")
                .append("adapter = ?, ")
                .append("temperature = ?, ")
                .append("broke = ?, ")
                .append("chargesta = ?, ")
                .append("colorid = ?, ")
                .append("cable = ? ")
                .append("WHERE rfid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("BATT_TYPE"),
                Boolean.parseBoolean(reqMap.get("KEY")),
                //new Timestamp(Long.valueOf(reqMap.get("TIME")) * 1000),// 归还时间
                reqMap.get("STATIONID"),
                slot,
                reqMap.get("POWER"),
                Boolean.parseBoolean(reqMap.get("ISDAMAGE")),
                reqMap.get("VOLTAGE"),
                reqMap.get("CURRENTVAL"),
                Boolean.parseBoolean(reqMap.get("ADAPTER")),
                reqMap.get("TEMPERATURE"),
                Boolean.parseBoolean(reqMap.get("BROKE")),
                Boolean.parseBoolean(reqMap.get("CHARGESTA")),
                reqMap.get("COLORID"),
                Integer.valueOf(reqMap.get("CABLE")),
                reqMap.get("ID")
        ));
    }

    @Override
    public void updateBatteryFromRentconfirm(Map<String, String> reqMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_battery ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:rentconfirm', ")
                .append("lastModifiedDate = NOW(), ")
                .append("stationid = NULL, ")
                .append("slot = NULL, ")
                .append("orderid = ? ")
                .append("WHERE rfid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("ORDERID"),
                reqMap.get("ID")
        ));
    }

    @Override
    public void syncBatteryInfoNotINStation(String slot, Map<String, String> reqMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_battery ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("sync_time = NOW(), ")
                .append("stationid = NULL, ")
                .append("slot = NULL ")
                .append("WHERE stationid = ? ")
                .append("AND slot = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("STATIONID"),
                slot
        ));
    }
}
