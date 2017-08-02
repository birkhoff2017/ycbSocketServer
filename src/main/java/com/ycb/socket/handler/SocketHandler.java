package com.ycb.socket.handler;


import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;

import java.text.ParseException;

public interface SocketHandler {
    void execute(MessageReq messageReq, MessageRes messageRes) throws ParseException;
}
