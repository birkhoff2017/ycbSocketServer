package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.StationDao;
import com.ycb.socket.model.Station;
import com.zipeiyi.xpower.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
        sql.append("SELECT sid FROM ycb_mcs_station ")
                .append("WHERE ")
                .append("mac = ? ");
        return dao.queryUniq(new DefaultOpUniq<Long>(sql, bizName).setMapper((resultSet, i) -> resultSet.getLong("sid")).addParams(mac));
    }

    @Override
    public Long insertNewStation(Map<String, String> reqMap) {
        String usableBattery = getUsableBattery(reqMap);
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ycb_mcs_station(createdBy,createdDate,optlock,sid,mac,ccid,power_on_time,usable,empty,usable_battery,total,route,heart_cycle,last_power_off_time,net_status) ")
                .append("VALUES('SYS:station', NOW(), 0,(SELECT MAX(sid) +1 FROM ycb_mcs_station cust), ?, ?, 0, ?, ?, ?, ?, ?, ?, ?, 1)");
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

    @Override
    public void updateStationInfo(Map<String, String> reqMap) {
        String usableBattery = getUsableBattery(reqMap);
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_station ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("net_status = 1, ")
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
                .append("WHERE sid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("DEVICE_VER"),
                new Timestamp(Long.valueOf(reqMap.get("TIMESTAMP")) * 1000),
                reqMap.get("STATIONID")
        ));
    }

    @Override
    public void updateStationBatteryInfo(Map<String, String> reqMap) {
        String usableBattery = getUsableBattery(reqMap);
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_station ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("usable = ?, ")
                .append("empty = ?, ")
                .append("total = ?, ")
                .append("usable_battery = ? ")
                .append("WHERE sid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("USABLE_BATTERY"),
                reqMap.get("EMPTY_SLOT_COUNT"),
                reqMap.get("TOTAL"),
                usableBattery,
                reqMap.get("STATIONID")
        ));
    }

    @Override
    public void updateStationFromBatterySync(Map<String, String> reqMap) {
        String usableBattery = getUsableBattery(reqMap);
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_station ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("slotstatus = ?, ")
                .append("usable = ?, ")
                .append("empty = ?, ")
                .append("total = ?, ")
                .append("usable_battery = ? ")
                .append("WHERE sid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                reqMap.get("SLOTSTATUS"),
                reqMap.get("USABLE_BATTERY"),
                reqMap.get("EMPTY_SLOT_COUNT"),
                reqMap.get("TOTAL"),
                usableBattery,
                reqMap.get("STATIONID")
        ));
    }

    @Override
    public void updateNetStatusById(String stationid) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_station ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:station', ")
                .append("lastModifiedDate = NOW(), ")
                .append("net_status = 0 ")
                .append("WHERE sid = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                stationid
        ));
    }

    private String getUsableBattery(Map<String, String> reqMap) {
        String usableBattery = reqMap.get("USABLE_BATTERY_NEW");
        if (!StringUtils.isEmpty(usableBattery)) {
            usableBattery = usableBattery.replaceAll("_", "\":\"");
            usableBattery = usableBattery.replaceAll("#", "\",\"");
            usableBattery = "{\"" + usableBattery + "\"}";
        } else {
            usableBattery = this.getUsableByStationid(reqMap.get("STATIONID"));
            if (StringUtils.isEmpty(usableBattery)) {
                usableBattery = "{\"1\":\"0\",\"2\":\"0\",\"3\":\"0\"}";
            }
        }
        return usableBattery;
    }

    public String getUsableByStationid(String stationid) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT usable_battery FROM ycb_mcs_station ")
                .append("WHERE ")
                .append("sid = ? ");
        return dao.queryUniq(new DefaultOpUniq<String>(sql, bizName).setMapper((resultSet, i) -> resultSet.getString("usable_battery")).addParams(stationid));
    }

    @Override
    public String findSyncSettingByStationid(String stationid) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT sync_setting from ycb_mcs_station WHERE sid = ?");
        return dao.queryUniq(new DefaultOpUniq<String>(sql, bizName).setMapper((resultSet, i) -> resultSet.getString("sync_setting")).addParams(stationid));
    }

    @Override
    public String getSecondaryValue(String key) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT secondaryValue FROM sys_DataDict WHERE parent_id = '58' AND primaryKey  = ?");
        return dao.queryUniq(new DefaultOpUniq<String>(sql, bizName).setMapper((resultSet, i) -> resultSet.getString("secondaryValue")).addParams(key));
    }

    @Override
    public Station findStationInfo(String stationid) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT device_ver,heartbeat_rate,heart_cycle,soft_ver from ycb_mcs_station WHERE sid = ? ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            Station station = null;
            while (rs.next()) {
                station = new Station();
                station.setDeviceVer(rs.getInt("device_ver"));
                station.setHeartbeatRate(rs.getInt("heartbeat_rate"));
                station.setHeartCycle(rs.getInt("heart_cycle"));
                station.setSoftVer(rs.getInt("soft_ver"));
            }
            return station;
        }).addParams(stationid));
    }

}
