package com.ycb.socket.handler;


import com.ycb.socket.message.MessageReq;
import com.ycb.socket.message.MessageRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class CardListHandler implements SocketHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(MessageReq request, MessageRes response) throws ParseException {

    }
}
