package com.ycb.socket.service;

import com.ycb.socket.dao.StationDao;
import com.ycb.socket.model.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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

    /**
     * 根据设备id查询设备的同步IP和port
     *
     * @param stationid
     * @return
     */
    public Map<String, String> findSyncSettingByStationid(String stationid) {
        String syncSetting = this.stationDao.findSyncSettingByStationid(stationid);
        String syncIpAndPort = "";
        //判断设备是否设置了同步策略，如果没有设置就采用默认策略
        if (null == syncSetting || "".equals(syncSetting)) {
            syncIpAndPort = this.stationDao.getSecondaryValue("default");
        } else {
            syncIpAndPort = this.stationDao.getSecondaryValue(syncSetting.toString());
        }
        //当设备没有设置同步策略并且字典表中也没有默认策略时，采用线上的服务器
        if (null == syncIpAndPort || "".equals(syncIpAndPort)) {
            syncIpAndPort = "ip:39.108.14.135;port:54589";
        }
        //syncIpAndPort格式为：ip:127.0.0.1;port:8000
        String[] split = syncIpAndPort.split(";");
        String ip = split[0].split(":")[1];
        String port = split[1].split(":")[1];
        Map<String, String> map = new LinkedHashMap<>();
        map.put("IP", ip);
        map.put("PORT", port);
        return map;
    }

    /**
     * 根据sid查询设备信息
     *
     * @param stationid
     * @return
     */
    public Station findStationInfo(String stationid) {
        return this.stationDao.findStationInfo(stationid);
    }
}