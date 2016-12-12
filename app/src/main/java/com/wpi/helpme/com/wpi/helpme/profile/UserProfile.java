package com.wpi.helpme.com.wpi.helpme.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the user profile object to be stored in the database.
 */
public class UserProfile {
    private String userId;
    private String userName;
    private String email;
    private List<String> filters = new ArrayList<>();
    private String deviceToken;

    /**
     * Creates an empty UserProfile instance.
     */
    public UserProfile() {
        this.userId = "";
        this.email = "";
        this.userName = "";
        this.deviceToken = "";
    }

    /**
     * Creates a UserProfile instance with a dummy device token.
     *
     * @param userId
     *         The unique user ID.
     * @param email
     *         The email address.
     * @param userName
     *         The display user name.
     */
    public UserProfile(String userId, String email, String userName) {
        this(userId, email, userName, "");
    }

    /**
     * Creates a UserProfile instance.
     *
     * @param userId
     *         The unique user ID.
     * @param email
     *         The email address.
     * @param userName
     *         The display user name.
     * @param deviceToken
     *         The registration token for push notifications.
     */
    public UserProfile(String userId, String email, String userName, String deviceToken) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.deviceToken = deviceToken;

        filters.add("test_value_1");
        filters.add("test_value_2");
        filters.add("test_value_3");
    }

    /**
     * @return a String
     * @see {@link Object#toString()}
     */
    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", filters=" + filters +
                '}';
    }

    /**
     * Returns the unique user ID.
     *
     * @return a String
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the email address.
     *
     * @return a String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the display user name.
     *
     * @return a String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the list of filters for the user.
     *
     * @return a {@link List<String>}
     */
    public List<String> getFilters() {
        return filters;
    }

    /**
     * Returns the device token for notifications.
     *
     * @return a String
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Updates the value of the device token.
     *
     * @param deviceToken
     *         The new registration token.
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
