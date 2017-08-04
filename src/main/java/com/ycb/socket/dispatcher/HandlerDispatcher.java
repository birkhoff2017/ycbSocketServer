package com.ycb.socket.dispatcher;

import com.ycb.socket.domain.ChannelRequest;
import com.ycb.socket.domain.MessageQueue;
import com.ycb.socket.handler.SocketHandler;
import com.ycb.socket.message.MessageRes;
import com.ycb.socket.utils.ExceptionUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class HandlerDispatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HandlerDispatcher.class);
    private final Map<Integer, MessageQueue> sessionMsgQ;
    private Map<String, SocketHandler> handlerMap;
    private Executor messageExecutor;
    private boolean running;
    private long sleepTime;

    public HandlerDispatcher() {
        this.sessionMsgQ = new ConcurrentHashMap<Integer, MessageQueue>();
        this.running = true;
        this.sleepTime = 200L;
    }

    public void setHandlerMap(Map<String, SocketHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void setMessageExecutor(Executor messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void addMessageQueue(Integer channelCode, MessageQueue messageQueue) {
        this.sessionMsgQ.put(channelCode, messageQueue);
    }

    public void removeMessageQueue(Channel channel) {
        MessageQueue queue = this.sessionMsgQ.remove(channel.hashCode());
        if (queue != null)
            queue.clear();
    }

    public MessageQueue getUserMessageQueue(Channel channel) {
        return this.sessionMsgQ.get(channel.hashCode());
    }

    public void addMessage(Channel channel, ChannelRequest request) {
        try {
            MessageQueue messageQueue = getUserMessageQueue(channel);
            if (messageQueue == null) {
                messageQueue = new MessageQueue(new ConcurrentLinkedQueue<>());
                messageQueue.add(request);
                addMessageQueue(channel.hashCode(), messageQueue);
            } else {
                messageQueue.add(request);
            }
        } catch (Exception e) {
            HandlerDispatcher.logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void run() {
        while (this.running) {
            try {
                sessionMsgQ.values().stream().filter(
                        messageQueue -> (messageQueue != null) && (messageQueue.size() > 0) && (!messageQueue.isRunning())).forEach(
                        messageQueue -> {
                            MessageWorker messageWorker = new MessageWorker(messageQueue);
                            this.messageExecutor.execute(messageWorker);
                        });
            } catch (Exception e) {
                HandlerDispatcher.logger.error(ExceptionUtils.getStackTrace(e));
            }
            try {
                Thread.sleep(this.sleepTime);
            } catch (InterruptedException e) {
                HandlerDispatcher.logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void stop() {
        this.running = false;
    }

    private final class MessageWorker implements Runnable {
        private final MessageQueue messageQueue;
        private ChannelRequest request;

        private MessageWorker(MessageQueue msgQueue) {
            msgQueue.setRunning(true);
            this.messageQueue = msgQueue;
            this.request = msgQueue.getRequestQueue().poll();
        }

        public void run() {
            try {
                String act = this.request.getMessageReq().getActValue();
                SocketHandler handler = HandlerDispatcher.this.handlerMap.get(act);
                MessageRes res = new MessageRes();
                if (handler != null) {
                    handler.execute(this.request.getMessageReq(), res);
                }
                request.getChannel().writeAndFlush(res);
            } catch (Exception e) {
                HandlerDispatcher.logger.error(ExceptionUtils.getStackTrace(e));
            } finally {
                    this.messageQueue.setRunning(false);
            }
        }
    }
}
