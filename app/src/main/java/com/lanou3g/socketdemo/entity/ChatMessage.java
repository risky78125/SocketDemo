package com.lanou3g.socketdemo.entity;

/**
 * 本类由: Risky57 创建于: 16/3/22.
 */
public class ChatMessage {
    public static final int MODE_SEND = 0;
    public static final int MODE_RECEIVE = 1;
    private String content;
    private String time;
    private String sendName;
    private int msgMode;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public int getMsgMode() {
        return msgMode;
    }

    public void setMsgMode(int msgMode) {
        this.msgMode = msgMode;
    }
}
