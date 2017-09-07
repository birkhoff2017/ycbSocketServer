package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.service.StationService;
import com.ycb.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-2.
 */
public class PopupConfirmHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            StationService stationService = NettyServerStart.factory.getBean(StationService.class);
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            stationService.updateStationFromBatterySync(reqMap);
            messageRes.setMsg("ERRCODE:0;ERRMSG:none;SLOT:" + reqMap.get("SLOT") + ";ACK:" + messageReq.getActValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
