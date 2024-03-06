package com.example.circleapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Attendee implements Parcelable {
    private String ID;
    private String firstName;
    private String lastName = null;
    private String email;
    private int phoneNumber = 0;
    private List<Event> events;
    private boolean isGeoEnabled;
    private Uri profilePic;

    public Attendee(String ID, String name, String email) {
        this.ID = ID;
        this.firstName = name;
        this.email = email;
    }

    // for Parcelable
    protected Attendee(Parcel in) {
        firstName = in.readString();
        email = in.readString();
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

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Event> getEvents() { return events; }

    public boolean isGeoEnabled() { return isGeoEnabled; }

    public void setGeoEnabled(boolean geoEnabled) { isGeoEnabled = geoEnabled; }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Uri getProfilePic() {
        return profilePic;
    }

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
        dest.writeString(firstName);
        dest.writeString(email);
        dest.writeByte((byte) (isGeoEnabled ? 1 : 0));
    }
}