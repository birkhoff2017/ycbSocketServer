package com.ycb.socket.dao;

import java.util.Map;

/**
 * Created by zhuhui on 17-8-11.
 */
public interface BatteryDao {
    Integer findByBatteryId(String id);

    void updateBatteryInfo(Map<String, String> reqMap);

    void insertBatteryInfo(Map<String, String> reqMap);

    void syncBatteryInfo(String slot, Map<String, String> reqMap);

    void updateBatteryFromRentconfirm(Map<String, String> reqMap);

    void syncBatteryInfoNotINStation(String slot, Map<String, String> reqMap);
}
