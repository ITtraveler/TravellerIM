package com.travelerim.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/21.
 */
public class ChatEntity implements Serializable {
    public static final int RECEIVE = 0;
    public static final int SEND = 1;
    public static final int TIME = 2;
    private int senderId;
    private int receiverId;
    private String sendDate = "0";
    private int messageType;
    private String content;

    public ChatEntity(String time, String content, int messageType) {
        this.sendDate = time;
        this.content = content;
        this.messageType = messageType;
    }

    public ChatEntity(String content, int messageType) {
        this.content = content;
        this.messageType = messageType;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getSendTime() {
        return sendDate;
    }

    public void setSendTime(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
