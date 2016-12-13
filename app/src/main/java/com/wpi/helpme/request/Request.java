package com.wpi.helpme.request;


public class Request {
    private String topic;
    private String notes;
    private String location;
    private String time;

    public Request() {
        this.topic = "";
        this.notes = "";
    }

    public Request(String topic, String notes) {
        this.topic = topic;
        this.notes = notes;
    }

    public String getTopic() {
        return topic;
    }

    public String getNotes() {
        return notes;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }
}

