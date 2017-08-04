package com.ycb.socket.handler;

import com.ycb.socket.NettyServerStart;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.service.StationService;

import java.text.ParseException;

/**
 * Created by zhuhui on 17-8-2.
 */
public class LoginHandler implements SocketHandler {
    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        // 根据MAC查询，没有新建设备入库
        StationService stationService = NettyServerStart.factory.getBean(StationService.class);
        String mac = messageReq.getContent().split(";")[1].split(":")[1];
        stationService.getStationIdByMac(mac);
    }
}
