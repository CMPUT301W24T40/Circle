package com.example.circleapp.BaseObjects;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This class represents an attendee of an event.
 */
public class Attendee implements Parcelable {
    private String ID; // Unique identifier for the attendee
    private String firstName; // First name of the attendee
    private String lastName; // Last name of the attendee
    private String email; // Email address of the attendee
    private String phoneNumber; // Phone number of the attendee
    private ArrayList<Event> events; // List of events the attendee is associated with
    private boolean isGeoEnabled; // Indicates if the attendee has geo-location enabled
    private Uri profilePic; // URI to the profile picture of the attendee

    /**
     * Constructs an Attendee object with specified parameters.
     *
     * @param ID          Unique identifier for the attendee
     * @param firstName   First name of the attendee
     * @param lastName    Last name of the attendee
     * @param email       Email address of the attendee
     * @param phoneNumber Phone number of the attendee
     * @param profilePic  URI to the profile picture of the attendee
     */
    public Attendee(String ID, String firstName, String lastName, String email, String phoneNumber, Uri profilePic) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
    }

    // Parcelable constructor
    protected Attendee(Parcel in) {
        ID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        isGeoEnabled = in.readByte() != 0;

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
     * @return The first name
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
     * Gets the list of events associated with the attendee.
     *
     * @return The list of events
     */
    public ArrayList<Event> getEvents() {
        return events;
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
     * Sets the first name of the attendee.
     *
     * @param name The new first name
     */
    public void setFirstName(String name) {
        this.firstName = name;
    }

    /**
     * Sets the last name of the attendee.
     *
     * @param lastName The new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the email address of the attendee.
     *
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the phone number of the attendee.
     *
     * @param phoneNumber The new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets whether geo-location is enabled for the attendee.
     *
     * @param geoEnabled True if geo-location is enabled, false otherwise
     */
    public void setGeoEnabled(boolean geoEnabled) {
        isGeoEnabled = geoEnabled;
    }

    /**
     * Sets the URI to the profile picture of the attendee.
     *
     * @param profilePic The new URI to the profile picture
     */
    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
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
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isGeoEnabled ? 1 : 0));
        dest.writeString(profilePic != null ? profilePic.toString() : null);
    }
}