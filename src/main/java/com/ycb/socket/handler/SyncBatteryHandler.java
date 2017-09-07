package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.service.BatteryService;
import com.ycb.socket.service.StationService;
import com.ycb.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

/**
 * 同步电池信息
 * Created by zhuhui on 17-8-2.
 */
public class SyncBatteryHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            StationService stationService = NettyServerStart.factory.getBean(StationService.class);
            BatteryService batteryService = NettyServerStart.factory.getBean(BatteryService.class);
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            stationService.updateStationFromBatterySync(reqMap);
            // 获取槽位有电池的电池信息
            Map<String, Object> batMap = StringUtils.converBatMap(reqMap);
            Iterator<Map.Entry<String, Object>> entries = batMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                batteryService.syncBatteryInfo(entry.getKey(), (Map<String, String>) entry.getValue());
            }
            messageRes.setMsg("ERRCODE:0;ERRMSG:none;ACK:" + messageReq.getActValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
