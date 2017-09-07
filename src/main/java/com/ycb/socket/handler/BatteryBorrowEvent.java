package com.ycb.socket.handler;

import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by zhuhui on 17-8-14.
 */
public class BatteryBorrowEvent implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            Map<String, String> reqMap = StringUtils.str2Map(messageReq.getContent());
            messageRes.setMsg(
                    "EVENT_CODE:" + reqMap.get("EVENT_CODE") +
                    ";ORDERID:" + reqMap.get("ORDERID") +
                    ";COLORID:" + reqMap.get("COLORID") +
                    ";CABLE:" + reqMap.get("CABLE") +
                    ";MSG_ID:" + System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        } finally {
            logger.info(messageReq.getContent());
            logger.info(messageRes.getMsg());
        }
    }
}
