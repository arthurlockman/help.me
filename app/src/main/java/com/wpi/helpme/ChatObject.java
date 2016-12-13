package com.wpi.helpme;

import java.util.Date;

/**
 * Created by Joey on 12/12/16.
 */

public class ChatObject {

    private String chatTitle;

    public ChatObject(String title) {
        this.chatTitle = title;
    }

    public ChatObject(){
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setTitle(String title) {
        this.chatTitle = title;
    }


}
