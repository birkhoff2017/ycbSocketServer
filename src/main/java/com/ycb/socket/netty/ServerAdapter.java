package com.ycb.socket.netty;

import com.ycb.socket.dispatcher.HandlerDispatcher;
import com.ycb.socket.domain.ChannelRequest;
import com.ycb.socket.message.MessageReq;
import com.ycb.socket.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAdapter extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ServerAdapter.class);

    private HandlerDispatcher handlerDispatcher;

    public ServerAdapter(HandlerDispatcher handlerDispatcher) {
        this.handlerDispatcher = handlerDispatcher;
    }

    private static Map<String, ChannelHandlerContext> cacheMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageReq req = new MessageReq();
        String msgStr = (String) msg;
        Map<String, String> reqMap = StringUtils.str2Map(msgStr);
        if (reqMap.containsKey("ACT") && reqMap.get("ACT").equals("sync_setting")) {
            cacheMap.put(reqMap.get("STATIONID"), ctx);
        }
        if (reqMap.containsKey("ACT") && reqMap.get("ACT").equals("heartbeat")) {
            cacheMap.put(reqMap.get("STATIONID"), ctx);
        } else if (reqMap.containsKey("EVENT_CODE")) {
            ctx = cacheMap.get(reqMap.get("STATIONID"));
        }
        String act = reqMap.get("ACT");
        req.setActValue(act);
        req.setContent(msgStr);
        ChannelRequest channelRequest = new ChannelRequest(ctx.channel(), req);
        this.handlerDispatcher.addMessage(ctx.channel(), channelRequest);
        logger.info("request act :[" + act + "] running!");
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ServerAdapter handler error,msg：" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        StationService stationService = NettyServerStart.factory.getBean(StationService.class);
//        for (Map.Entry<String, ChannelHandlerContext> entry : cacheMap.entrySet()) {
//            if (entry.getValue().equals(ctx)) {
//                stationService.updateNetStatusById(entry.getKey());
//            }
//        }
//        ctx.close();
//    }
}
