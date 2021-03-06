package com.ycb.socket.handler;

import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * Created by zhuhui on 17-8-2.
 */
public class DebugHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException {
        try {
            messageRes.setMsg("ERRCODE:0;ERRMSG:none" + ";ACK:" + messageReq.getActValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
            messageRes.setMsg("ERRCODE:0;ERRMSG:" + e.getMessage() + ";ACK:" + messageReq.getActValue());
        }
    }
}
