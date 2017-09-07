package com.ycb.socket.utils;

/**
 * Created by yunchongba on 2017/8/23.
 */
public class TimeUtil {

    public static String timeToString(Long duration) {
        String lastTime;
        try {
            if (null != duration || 0 != duration) {
                Long days = duration / (60 * 60 * 24);   //天
                Long hours = (duration - days * (60 * 60 * 24)) / (60 * 60);  //小时
                Long minutes = (duration - days * (60 * 60 * 24) - hours * (60 * 60)) / 60; //分
                Long ss = (duration - days * (60 * 60 * 24) - hours * (60 * 60) - (minutes * 60));
                if (0 != days.longValue()) {
                    lastTime = "" + days + "天" + hours + "小时" + minutes + "分" + ss + "秒";
                    return lastTime;
                } else if (0 != hours.longValue()) {
                    lastTime = "" + hours + "小时" + minutes + "分" + ss + "秒";
                    return lastTime;
                } else if (0 != minutes.longValue()) {
                    lastTime = "" + minutes + "分" + ss + "秒";
                    return lastTime;
                } else if (0 != ss.longValue()) {
                    lastTime = "" + ss + "秒";
                    return lastTime;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * 获取费用设置的时间单位
     *
     * @param maxFeeUnit
     * @return
     */
    public static String getUnitString(Long maxFeeUnit) {
        String unit = "";
        if (maxFeeUnit.equals(1l)) {
            unit = "秒";
        } else if (maxFeeUnit.equals(60l)) {
            unit = "分钟";
        } else if (maxFeeUnit.equals(3600l)) {
            unit = "小时";
        } else if (maxFeeUnit.equals(86400l)) {
            unit = "天";
        }
        return unit;
    }
}
