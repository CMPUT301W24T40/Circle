
package com.example.circleapp.BaseObjects;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Attendee implements Parcelable {
    private String ID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private ArrayList<Event> events;
    private boolean isGeoEnabled;
    private Uri profilePic;

    public Attendee(String ID, String firstName, String lastName, String email, String phoneNumber, Uri profilePic) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
    }

    // for Parcelable
    protected Attendee(Parcel in) {
        ID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        isGeoEnabled = in.readByte() != 0;
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

    public String getID() { return ID; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<Event> getEvents() { return events; }

    public boolean isGeoEnabled() { return isGeoEnabled; }

    public Uri getProfilePic() {
        return profilePic;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGeoEnabled(boolean geoEnabled) { isGeoEnabled = geoEnabled; }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isGeoEnabled ? 1 : 0));
    }
}