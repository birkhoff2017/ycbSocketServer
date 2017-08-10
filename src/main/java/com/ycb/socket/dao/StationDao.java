package com.ycb.socket.dao;

import java.util.Map;

/**
 * Created by zhuhui on 17-8-3.
 */
public interface StationDao {
    Long getStationIdByMac(String mac);

    Long insertNewStation(Map<String, String> reqMap);

    void updateStationInfo(Map<String, String> reqMap);

    void updateSyncSetting(Map<String, String> reqMap);
}