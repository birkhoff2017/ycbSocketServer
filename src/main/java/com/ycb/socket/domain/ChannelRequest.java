package com.ycb.socket.domain;

import com.ycb.socket.message.MessageReq;
import io.netty.channel.Channel;

/**
 * Created by zhuhui on 16-12-22.
 */
public class ChannelRequest {
    private Channel channel;
    private MessageReq messageReq;

    public ChannelRequest(Channel channel, MessageReq messageReq) {
        this.channel = channel;
        this.messageReq = messageReq;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public MessageReq getMessageReq() {
        return messageReq;
    }

    public void setMessageReq(MessageReq messageReq) {
        this.messageReq = messageReq;
    }
}
