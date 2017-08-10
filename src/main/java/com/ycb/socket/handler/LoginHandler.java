package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.service.StationService;
import com.ycb.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 设备登入
 * Created by zhuhui on 17-8-2.
 */
public class LoginHandler implements SocketHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) {
        try {
            // 根据MAC查询，新设备入库，老设备更新状态
            StationService stationService = NettyServerStart.factory.getBean(StationService.class);
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            String mac = reqMap.get("MAC");
            Long sid = stationService.getStationIdByMac(mac);
            if (sid == null) {
                sid = stationService.insertNewStation(reqMap);
            } else {
                stationService.updateStationInfo(reqMap);
            }
            messageRes.setMsg("BINDADDRESS:" + sid + ";STATIONID:" + sid + ";");
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        }
    }
}
