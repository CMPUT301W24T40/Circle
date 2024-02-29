package com.example.circleapp;

import java.util.List;

public class Attendee {
    private String name;
    private String email;
    private List<Event> events;
    private boolean isGeoEnabled;
    // private profilePic

    public Attendee(String name, String email) {
        this.name = name;
        this.email = email;
    }

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
}