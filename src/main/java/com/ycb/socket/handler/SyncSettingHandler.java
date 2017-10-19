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
            messageRes.setMsg("TIME:" +
                    System.currentTimeMillis() / 1000
                    + ";DOMAIN:"+reqMap.get("DOMAIN")+";IP:"+reqMap.get("IP")+";PORT:"+reqMap.get("PORT")+";" +
                    "CHECKUPDATEDELAY:1;SOFT_VER:" + reqMap.get("SOFT_VER") + ";FILE_NAME:null;HEATBEAT:"+reqMap.get("HEATBEAT"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
