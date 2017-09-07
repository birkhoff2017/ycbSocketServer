package com.ycb.socket.service;

import com.ycb.socket.dao.BatteryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuhui on 17-8-10.
 */
@Service
public class BatteryService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BatteryDao stationDao;

    public Boolean findByBatteryId(String id) {
        Integer count = stationDao.findByBatteryId(id);
        if (count == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void updateBatteryInfo(Map<String, String> reqMap) {
        stationDao.updateBatteryInfo(reqMap);
    }


    public void insertBatteryInfo(Map<String, String> reqMap) {
        stationDao.insertBatteryInfo(reqMap);
    }

    public void syncBatteryInfo(String slot, Map<String, String> reqMap) {
        if (!"0000000000".equals(reqMap.get("ID"))) {
            // 根据电池ID检索电池表，新建数据
            Boolean exist = this.findByBatteryId(reqMap.get("ID"));
            if (exist) {
                stationDao.syncBatteryInfo(slot, reqMap);
            } else {
                // 新增电池
                this.insertBatteryInfo(reqMap);
            }
        } else {
            // 更新电池设备槽位
            stationDao.syncBatteryInfoNotINStation(slot, reqMap);
        }
    }

    public void updateBatteryFromRentconfirm(Map<String, String> reqMap) {
        stationDao.updateBatteryFromRentconfirm(reqMap);
    }
}