package com.ycb.socket.service;

import com.ycb.socket.dao.StationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuhui on 17-8-3.
 */
@Service
public class StationService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StationDao stationDao;

    public Long getStationIdByMac(String mac) {
        return stationDao.getStationIdByMac(mac);
    }

    public Long insertNewStation(Map<String, String> reqMap) {
        return stationDao.insertNewStation(reqMap);
    }

    public void updateStationInfo(Map<String, String> reqMap) {
        stationDao.updateStationInfo(reqMap);
    }

    public void updateSyncSetting(Map<String, String> reqMap) {
        stationDao.updateSyncSetting(reqMap);
    }

    public void updateStationBatteryInfo(Map<String, String> reqMap) {
        stationDao.updateStationBatteryInfo(reqMap);
    }

    public void updateStationFromBatterySync(Map<String, String> reqMap) {
        stationDao.updateStationFromBatterySync(reqMap);
    }

    public void updateStationFromRentconfirm(Map<String, String> reqMap) {
        stationDao.updateStationBatteryInfo(reqMap);
    }

    public void updateNetStatusById(String stationid) {
        stationDao.updateNetStatusById(stationid);
    }
}