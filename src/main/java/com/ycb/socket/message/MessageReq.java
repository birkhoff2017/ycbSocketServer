package com.ycb.socket.message;

/**
 * create by zhuhui
 */
public class MessageReq implements Cloneable {
    String actValue;

    String content;

    public String getActValue() {
        return actValue;
    }

    public void setActValue(String actValue) {
        this.actValue = actValue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
