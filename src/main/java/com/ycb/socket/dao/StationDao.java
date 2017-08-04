package com.ycb.socket.dao;

/**
 * Created by zhuhui on 17-8-3.
 */
public interface StationDao {
    void createStationByMac(String mac);

    Long getStationIdByMac(String mac);
}