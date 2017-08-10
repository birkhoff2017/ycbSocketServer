package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.StationDao;
import com.zipeiyi.xpower.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-3.
 */
@Repository
public class StationDaoImpl implements StationDao {
    final IDao dao = DaoFactory.getIDao();
    final String bizName = "ycb";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Long getStationIdByMac(String mac) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT id FROM ycb_mcs_station ")
                .append("WHERE ")
                .append("mac = ? ");
        return dao.queryUniq(new DefaultOpUniq<Long>(sql, bizName).setMapper((resultSet, i) -> resultSet.getLong("id")).addParams(mac));
    }

    @Override
    public Long insertNewStation(Map<String, String> reqMap) {
        String usableBattery = getUsableBattery(reqMap);
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ycb_mcs_station(createdBy,createdDate,optlock,mac,ccid,power_on_time,usable,empty,usable_battery,total,route,heart_cycle,last_power_off_time)")
                .append("VALUES('SYS:station', NOW(), 0, ?, ?, 0, ?, ?, ?, ?, ?, ?, ?)");
        return dao.insert(new OpInsert<>(sql, bizName, Long.class)
                .addParams(
                        reqMap.get("MAC"),
                        reqMap.get("CCID"),
                        reqMap.get("USABLE_BATTERY"),
                        reqMap.get("EMPTY_SLOT_COUNT"),
                        usableBattery,
                        reqMap.get("TOTAL"),
                        reqMap.get("ROUTE"),
                        reqMap.get("HeartCycle"),
                        new Timestamp(Long.valueOf(reqMap.get("L_PD")) * 1000)
                ));
    }

    private String getUsableBattery(Map<String, String> reqMap) {
        String usableBattery = reqMap.get("USABLE_BATTERY_NEW");
        usableBattery = usableBattery.replaceAll("_", "\":\"");
        usableBattery = usableBattery.replaceAll("#", "\",\"");
        usableBattery = "{\"" + usableBattery + "\"}";
        return usableBattery;
    }

    @Override
    public void updateStationInfo(Map<String, String> reqMap) {
        String usableBattery = getUsableBattery(reqMap);
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_station ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("usable = ?, ")
                .append("empty = ?, ")
                .append("usable_battery = ?, ")
                .append("total = ?, ")
                .append("route = ?, ")
                .append("heart_cycle = ?, ")
                .append("last_power_off_time = ? ")
                .append("WHERE mac = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("USABLE_BATTERY"),
                reqMap.get("EMPTY_SLOT_COUNT"),
                usableBattery,
                reqMap.get("TOTAL"),
                reqMap.get("ROUTE"),
                reqMap.get("HeartCycle"),
                new Timestamp(Long.valueOf(reqMap.get("L_PD")) * 1000),
                reqMap.get("MAC")
        ));
    }

    @Override
    public void updateSyncSetting(Map<String, String> reqMap) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_station ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("device_ver = ?, ")
                .append("sync_time = ? ")
                .append("WHERE id = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("DEVICE_VER"),
                new Timestamp(Long.valueOf(reqMap.get("TIMESTAMP")) * 1000),
                reqMap.get("STATIONID")
        ));
    }
}
