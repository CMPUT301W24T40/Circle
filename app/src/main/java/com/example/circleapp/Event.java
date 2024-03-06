package com.example.circleapp;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Event implements Serializable { //May need to switch to Parcelable later
    private String eventName;
    private String location;
    private String date;
    private String time;
    private String description;
    private int eventPoster;
    // private detailsQRCode;
    // private registerQRCode;

    //Constructor
    public Event(String eventName, String location, String date, String time, String description, int eventPoster) {
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.eventPoster = eventPoster;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEventPoster() {
        return eventPoster;
    }
    public void setEventPoster(int eventPoster) {
        this.eventPoster = eventPoster;
    }
}
