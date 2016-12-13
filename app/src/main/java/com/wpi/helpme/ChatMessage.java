package com.wpi.helpme;

import java.util.Date;

/**
 * Created by Joey on 12/12/16.
 */

public class ChatMessage {

    private String msgText;
    private String msgUser;
    private long msgTime;

    public ChatMessage(String msgText, String msgeUser) {
        this.msgText = msgText;
        this.msgUser = msgUser;

        // Initialize to current time
        msgTime = new Date().getTime();

    }

    public ChatMessage(){

    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMessageTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
