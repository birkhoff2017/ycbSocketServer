package com.ycb.socket.netty;

import com.ycb.socket.dispatcher.HandlerDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private HandlerDispatcher handlerDispatcher;

    public void init() {
        new Thread(this.handlerDispatcher).start();
    }

    public void initChannel(SocketChannel ch) throws Exception {
        ChannelGroups.add(ch);
        ChannelPipeline pipeline = ch.pipeline();
        // 以("\n")为结尾分割的 解码器
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
        // 字符串解码 和 编码
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
//        pipeline.addLast("decoder", new LineBasedFrameDecoder(Integer.MAX_VALUE));
//        pipeline.addLast("encoder", new LineEncoder());
//        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
//        pipeline.addLast("protobufDecoder", new ServerDecoder());
//        pipeline.addLast("protobufEncoder", new ServerEncoder());
        pipeline.addLast("handler", new ServerAdapter(this.handlerDispatcher));
    }

    public void setHandlerDispatcher(HandlerDispatcher handlerDispatcher) {
        this.handlerDispatcher = handlerDispatcher;
    }
}