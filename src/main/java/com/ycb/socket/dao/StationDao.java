package com.ycb.socket.dao;

import com.ycb.socket.model.Station;

import java.util.Map;

/**
 * Created by zhuhui on 17-8-3.
 */
public interface StationDao {
    Long getStationIdByMac(String mac);

    Long insertNewStation(Map<String, String> reqMap);

    void updateStationInfo(Map<String, String> reqMap);

    void updateSyncSetting(Map<String, String> reqMap);

    void updateStationBatteryInfo(Map<String, String> reqMap);

    void updateStationFromBatterySync(Map<String, String> reqMap);

    void updateNetStatusById(String stationid);

    String findSyncSettingByStationid(String stationid);

    String getSecondaryValue(String key);

    Station findStationInfo(String stationid);
}