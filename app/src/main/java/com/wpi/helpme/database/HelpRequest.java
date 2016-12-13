package com.wpi.helpme.database;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a request object that is read from the database.
 */
public class HelpRequest {
    private List<String> topics;
    private String title;
    private String body;
    private double latitude;
    private double longitude;
    private String expireTime;
    private String userEmail;

    /**
     * Creates an empty HelpRequest instance.
     */
    public HelpRequest() {
        this.topics = new ArrayList<>();
        this.title = "";
        this.body = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.expireTime = "";
        this.userEmail = "";
    }

    /**
     * Creates a HelpRequest instance.
     *
     * @param topic
     *         The topic.
     * @param userEmail
     *         The user's email.
     * @param title
     *         The request title.
     * @param body
     *         The notes.
     * @param latitude
     *         The latitude.
     * @param longitude
     *         The longitude.
     * @param expireTime
     *         The expiration time.
     */
    public HelpRequest(String topic, String userEmail, String title, String body, double latitude,
                       double longitude, String expireTime) {
        this.topics = new ArrayList<>();
        this.topics.add(topic);
        this.userEmail = userEmail;
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expireTime = expireTime;

    }

    /**
     * Returns the expiration time.
     *
     * @return a String
     */
    public String getExpireTime() {
        return expireTime;
    }

    /**
     * Returns the user's email.
     *
     * @return a String
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Returns the topics in this request.
     *
     * @return a {@link List<String>}
     */
    public List<String> getTopics() {
        return topics;
    }

    /**
     * Returns the longitude.
     *
     * @return a double
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the body.
     *
     * @return a String
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the latitude
     *
     * @return a double
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the title.
     *
     * @return a String
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "HelpRequest{" +
                "topics=" + topics +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", expireTime=" + expireTime +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
