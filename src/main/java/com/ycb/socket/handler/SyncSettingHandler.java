package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.model.Station;
import com.ycb.socket.service.StationService;
import com.ycb.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Map;

/**
 * 同步配置
 * Created by zhuhui on 17-8-2.
 */
public class SyncSettingHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            StationService stationService = NettyServerStart.factory.getBean(StationService.class);
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            stationService.updateSyncSetting(reqMap);
            //查询出同步设置的IP和port
            Map<String, String> map = stationService.findSyncSettingByStationid(reqMap.get("STATIONID"));
            Station station = stationService.findStationInfo(reqMap.get("STATIONID"));
            messageRes.setMsg("TIME:" +
                    System.currentTimeMillis() / 1000
                    + ";DOMAIN:https://x.yunchongba.com;IP:" + map.get("IP") + ";PORT:" + map.get("PORT") + ";" +
                    "CHECKUPDATEDELAY:1;SOFT_VER:" + reqMap.get("SOFT_VER") + ";FILE_NAME:null;HEATBEAT:" + station.getHeartCycle());
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
