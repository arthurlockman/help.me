package com.wpi.helpme.database;

public class HelpRequest {
    private String bodyText;
    private double latitude;
    private double longitude;
    private String titleText;

    public HelpRequest(String titleText, String bodyText, double latitude, double longitude) {

        this.titleText = titleText;
        this.bodyText = bodyText;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "HelpRequest{" +
                "bodyText='" + bodyText + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", titleText='" + titleText + '\'' +
                '}';
    }

    public String getBodyText() {
        return bodyText;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTitleText() {
        return titleText;
    }
}
