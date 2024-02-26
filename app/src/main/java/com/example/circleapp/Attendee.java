package com.example.circleapp;

import java.lang.reflect.Array;

public class Attendee {
    private String name;
    private String email;
    // private profilePic
    private Array events;

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

    public Array getEvents() {
        return events;
    }

    public void setEvents(Array events) {
        this.events = events;
    }
}
