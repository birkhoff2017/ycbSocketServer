package com.ycb.socket.netty;

import com.ycb.socket.dispatcher.HandlerDispatcher;
import com.ycb.socket.domain.ChannelRequest;
import com.ycb.socket.message.MessageReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAdapter extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ServerAdapter.class);

    private HandlerDispatcher handlerDispatcher;

    public ServerAdapter(HandlerDispatcher handlerDispatcher) {
        this.handlerDispatcher = handlerDispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageReq req = new MessageReq();
        String msgStr = (String) msg;
        String act = msgStr.split(";")[0].split(":")[1];
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
        ChannelGroups.discard(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        ChannelGroups.discard(ctx.channel());
    }

}
