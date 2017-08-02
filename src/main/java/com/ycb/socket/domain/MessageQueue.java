package com.ycb.socket.domain;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private Queue<ChannelRequest> requestQueue;
    private boolean running = false;

    public MessageQueue(ConcurrentLinkedQueue<ChannelRequest> concurrentLinkedQueue) {
        this.requestQueue = concurrentLinkedQueue;
    }

    public Queue<ChannelRequest> getRequestQueue() {
        return this.requestQueue;
    }

    public void setRequestQueue(Queue<ChannelRequest> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void clear() {
        this.requestQueue.clear();
        this.requestQueue = null;
    }

    public int size() {
        return this.requestQueue != null ? this.requestQueue.size() : 0;
    }

    public boolean add(ChannelRequest request) {
        return this.requestQueue.add(request);
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return this.running;
    }
}