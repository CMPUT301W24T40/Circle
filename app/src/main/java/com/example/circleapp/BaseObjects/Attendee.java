package com.example.circleapp.BaseObjects;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * This class represents an attendee of an event.
 */
public class Attendee implements Parcelable {
    private String ID; // Unique identifier for the attendee
    private String firstName; // First name of the attendee
    private String lastName; // Last name of the attendee
    private String email; // Email address of the attendee
    private String phoneNumber; // Phone number of the attendee
    private boolean isGeoEnabled; // Indicates if the attendee has geo-location enabled
    private Uri profilePic; // URI to the profile picture of the attendee
    private String homepage;
    private boolean hasProfile; // Indicates if the user has a profile or not
    private String token; // for notifications
    private Double locationLatitude; // latitude of location of attendee
    private Double locationLongitude; // longitude of location of attendee

    /**
     * Constructs an Attendee object with specified parameters.
     *
     * @param ID          Unique identifier for the attendee
     * @param firstName   First name of the attendee
     * @param lastName    Last name of the attendee
     * @param email       Email address of the attendee
     * @param phoneNumber Phone number of the attendee
     * @param homepage    Homepage URL of the attendee
     * @param profilePic  URI to the profile picture of the attendee
     */
    public Attendee(String ID, String firstName, String lastName, String email, String phoneNumber, String homepage, Uri profilePic) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
        this.homepage = homepage;
    }

    // to avoid proguard error
    public Attendee() {
        // ahhhhh
    }

    // Parcelable constructor
    protected Attendee(Parcel in) {
        ID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        homepage = in.readString();
        phoneNumber = in.readString();
        isGeoEnabled = in.readByte() != 0;
        locationLatitude = in.readDouble();
        locationLongitude = in.readDouble();

        // Read the Uri as a String from the Parcel
        String uriString = in.readString();
        if (uriString != null) {
            profilePic = Uri.parse(uriString);
        }
    }

    public static final Creator<Attendee> CREATOR = new Creator<Attendee>() {
        @Override
        public Attendee createFromParcel(Parcel in) {
            return new Attendee(in);
        }

        @Override
        public Attendee[] newArray(int size) {
            return new Attendee[size];
        }
    };

    // Getters and setters

    /**
     * Gets the unique identifier of the attendee.
     *
     * @return The unique identifier
     */
    public String getID() {
        return ID;
    }

    /**
     * Gets the first name of the attendee.
     *
     * @return The first nameFirebaseManager manager
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the attendee.
     *
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address of the attendee.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the phone number of the attendee.
     *
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Checks if the geo-location is enabled for the attendee.
     *
     * @return True if geo-location is enabled, false otherwise
     */
    public boolean isGeoEnabled() {
        return isGeoEnabled;
    }

    /**
     * Gets the URI to the profile picture of the attendee.
     *
     * @return The URI to the profile picture
     */
    public Uri getProfilePic() {
        return profilePic;
    }

    /**
     * Gets the homepage of the attendee.
     *
     * @return The homepage URL
     */
    public String getHomepage(){
        return homepage;
    }

    /**
     * Checks if the user has a profile or not.
     *
     * @return True if user doesn't have a profile, false otherwise
     */
    public boolean hasProfile() {return hasProfile; }

    /**
     * Gets the token of the attendee.
     *
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets the latitude of location of the attendee.
     *
     * @return The latitude
     */
    public Double getLocationLatitude() {
        return locationLatitude;
    }

    /**
     * Gets the longitude of location of the attendee.
     *
     * @return The longitude
     */
    public Double getLocationLongitude() {
        return locationLongitude;
    }


    /**
     * Sets the first name of the attendee.
     *
     * @param name The new first name
     */
    public void setfirstName(String name) {
        this.firstName = name;
    }

    /**
     * Sets the last name of the attendee.
     *
     * @param lastName The new last name
     */
    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the email address of the attendee.
     *
     * @param email The new email address
     */
    public void setemail(String email) {
        this.email = email;
    }

    /**
     * Sets the phone number of the attendee.
     *
     * @param phoneNumber The new phone number
     */
    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets whether geo-location is enabled for the attendee.
     *
     * @param geoEnabled True if geo-location is enabled, false otherwise
     */
    public void setisGeoEnabled(boolean geoEnabled) {
        isGeoEnabled = geoEnabled;
    }

    /**
     * Sets the URI to the profile picture of the attendee.
     *
     * @param profilePic The new URI to the profile picture
     */
    public void setprofilePic(String profilePic) {
        this.profilePic = Uri.parse(profilePic);
    }

    /**
     * Sets the homepage of the attendee.
     *
     * @param homepage The new homepage URL
     */
    public void sethomepage(String homepage){
        this.homepage = homepage;
    }

    /**
     * Sets whether the user has a profile or not.
     *
     * @param hasProfile True if user doesn't have a profile, false otherwise
     */
    public void sethasProfile(boolean hasProfile) { this.hasProfile = hasProfile; }

    /**
     * Sets the token of the attendee.
     *
     * @param token The new token
     */
    public void settoken(String token) {
        this.token = token;
    }

    /**
     * Sets the location of the attendee.
     *
     * @param latitude The new latitude
     */
    public void setLocationLatitude(Double latitude) {
        this.locationLatitude = latitude;
    }

    /**
     * Sets the location of the attendee.
     *
     * @param longitude The new longitude
     */
    public void setLocationLongitude(Double longitude) {
        this.locationLongitude = longitude;
    }

    // Parcelable methods

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the object's data to the provided parcel.
     *
     * @param dest  The parcel to which the data should be written
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(homepage);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isGeoEnabled ? 1 : 0));
        dest.writeString(profilePic != null ? profilePic.toString() : null);
        dest.writeDouble(locationLatitude);
        dest.writeDouble(locationLongitude);
    }
}