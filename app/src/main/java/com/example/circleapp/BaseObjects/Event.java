package com.example.circleapp.BaseObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Event implements Parcelable {
    private String ID;
    private String eventName;
    private String location;
    private String date;
    private String time;
    private String description;
    private int eventPoster;

    //private detailsQRCode;

    //private registerQRCode;

    public Event(String ID, String eventName, String location, String date, String time, String description) {
        this.ID = ID;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    // for Parcelable
    protected Event(Parcel in) {
        ID = in.readString();
        eventName = in.readString();
        location = in.readString();
        date = in.readString();
        time = in.readString();
        description = in.readString();
    }

    // No-argument constructor
    public Event() {
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getID() {return ID; }

    public String getEventName() {
        return eventName;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public int getEventPoster() {
        return eventPoster;
    }

    public void setID(String ID) {this.ID = ID; }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventPoster(int eventPoster) {
        this.eventPoster = eventPoster;
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(eventName);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(description);
    }
}