package com.ycb.socket.service;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.dao.StationDao;
import com.ycb.socket.dao.impl.StationDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuhui on 17-8-3.
 */
@Service
public class StationService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public Long getStationIdByMac(String mac) {
        StationDao stationDao = NettyServerStart.factory.getBean(StationDaoImpl.class);
        return stationDao.getStationIdByMac(mac);
    }

    public Long insertNewStation(Map<String, String> reqMap) {
        StationDao stationDao = NettyServerStart.factory.getBean(StationDaoImpl.class);
        return stationDao.insertNewStation(reqMap);
    }

    public void updateStationInfo(Map<String, String> reqMap) {
        StationDao stationDao = NettyServerStart.factory.getBean(StationDaoImpl.class);
        stationDao.updateStationInfo(reqMap);
    }

    public void updateSyncSetting(Map<String, String> reqMap) {
        StationDao stationDao = NettyServerStart.factory.getBean(StationDaoImpl.class);
        stationDao.updateSyncSetting(reqMap);
    }
}