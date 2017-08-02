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
//        this.logger.info("[" + request.getCmd() + "] 游戏暗牌！");
//        response.setModuleId(request.getModuleId());
//        response.setCmd(request.getCmd());
//        response.setSeque(request.getSeque());
//        response.setUid(request.getUid());
//        response.setMsg("游戏暗牌！");
//        DBCardListReq req = (DBCardListReq) request.getObj();
//        GameService gameService = NettyServerStart.factory.getBean(GameService.class);
//        List<CardInfo> cardInfoList = gameService.getHideCard(req.getCardAmount());
//
//        DBCardListAck ack = new DBCardListAck();
//        ack.setCardList(cardInfoList);
//        response.setObj(ack);
    }
}
