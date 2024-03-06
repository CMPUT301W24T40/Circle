package com.example.circleapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Attendee implements Parcelable {
    private String ID;
    private String name;
    private String email;
    private List<Event> events;
    private boolean isGeoEnabled;
    private Uri profilePic;

    public Attendee(String ID, String name, String email) {
        this.ID = ID;
        this.name = name;
        this.email = email;
    }

    // for Parcelable
    protected Attendee(Parcel in) {
        name = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(name);
        dest.writeString(email);
        dest.writeByte((byte) (isGeoEnabled ? 1 : 0));
    }
}