package com.wpi.helpme.database;

/**
 * This class represents a request object that is read from the database.
 */
public class HelpRequest {
    private String bodyText;
    private double latitude;
    private double longitude;
    private String titleText;

    /**
     * Creates a HelpRequest instance.
     *
     * @param titleText
     *         The title.
     * @param bodyText
     *         The body.
     * @param latitude
     *         The latitude.
     * @param longitude
     *         The longitude.
     */
    public HelpRequest(String titleText, String bodyText, double latitude, double longitude) {

        this.titleText = titleText;
        this.bodyText = bodyText;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the longitude.
     *
     * @return a double
     */
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

    /**
     * Returns the body.
     *
     * @return a String
     */
    public String getBodyText() {
        return bodyText;
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
    public String getTitleText() {
        return titleText;
    }
}
