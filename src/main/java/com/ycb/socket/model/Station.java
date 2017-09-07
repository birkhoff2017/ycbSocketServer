package com.ycb.socket.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * 设备
 * Created by zhuhui on 17-8-3.
 */
public class Station implements Serializable {

    private static final long serialVersionUID = -3614572626931022107L;

    private Long id;

    // @MetaData("Mac地址")
    private String mac;

    // @MetaData("可借数")
    private Integer usable;

    // @MetaData("可还数")
    private Integer empty;

    // @MetaData("槽位状态")
    private String slotstatus;

    // "2:安卓苹果二合一线 3:TypeC线"
    // @MetaData("线类型")
    private Integer cable;

    // @MetaData("设备名")
    private String title;

    // "0,1,2,31,32,10,11,12,13,14,15,20"
    // @MetaData("设备版本")
    private Integer deviceVer;

    // @MetaData("心跳率")
    private Integer heartbeatRate;

    // @MetaData("开机时长")
    private Long powerOnTime;

    // @MetaData("同步时间")
    private Date syncTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getUsable() {
        return usable;
    }

    public void setUsable(Integer usable) {
        this.usable = usable;
    }

    public Integer getEmpty() {
        return empty;
    }

    public void setEmpty(Integer empty) {
        this.empty = empty;
    }

    public String getSlotstatus() {
        return slotstatus;
    }

    public void setSlotstatus(String slotstatus) {
        this.slotstatus = slotstatus;
    }

    public Integer getCable() {
        return cable;
    }

    public void setCable(Integer cable) {
        this.cable = cable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDeviceVer() {
        return deviceVer;
    }

    public void setDeviceVer(Integer deviceVer) {
        this.deviceVer = deviceVer;
    }

    public Integer getHeartbeatRate() {
        return heartbeatRate;
    }

    public void setHeartbeatRate(Integer heartbeatRate) {
        this.heartbeatRate = heartbeatRate;
    }

    public Long getPowerOnTime() {
        return powerOnTime;
    }

    public void setPowerOnTime(Long powerOnTime) {
        this.powerOnTime = powerOnTime;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }
}
