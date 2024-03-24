package com.example.circleapp.BaseObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * This class represents an event.
 */
public class Event implements Parcelable {
    private String ID;
    private String eventName;
    private String location;
    private String date;
    private String time;
    private String description;
    private String capacity;
    private String eventPosterURL; // Resource ID for the event poster
    private String checkInID; // Used for reusing QR codes

    // Constructors
    /**
     * Constructs an Event object with specified parameters.
     *
     * @param ID          Unique identifier for the event
     * @param eventName   Name of the event
     * @param location    Location of the event
     * @param date        Date of the event
     * @param time        Time of the event
     * @param description Description of the event
     */
    public Event(String ID, String eventName, String location, String date, String time, String description) {
        this.ID = ID;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    // No-argument constructor
    public Event() {}

    // Parcelable constructor
    protected Event(Parcel in) {
        ID = in.readString();
        eventName = in.readString();
        location = in.readString();
        date = in.readString();
        time = in.readString();
        description = in.readString();
        capacity = in.readString();
        eventPosterURL = in.readString();
        checkInID = in.readString();
    }

    // Parcelable creator
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

    // Getters and setters

    /**
     * Gets the unique identifier of the event.
     *
     * @return The unique identifier
     */
    public String getID() {
        return ID;
    }

    /**
     * Gets the name of the event.
     *
     * @return The event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets the location of the event.
     *
     * @return The event location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the date of the event.
     *
     * @return The event date
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time of the event.
     *
     * @return The event time
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the description of the event.
     *
     * @return The event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the maximum number of attendees allowed for the event,
     * chosen by the organizer.
     *
     * @return The maximum capacity
     */
    public String getCapacity() { return capacity; }

    /**
     * Gets the resource ID for the event poster.
     *
     * @return The resource ID
     */
    public String getEventPosterURL() {
        return eventPosterURL;
    }

    /**
     * Gets the check-in ID for the event.
     *
     * @return The check-in ID
     */
    public String getCheckInID() {return checkInID;}

    /**
     * Sets the unique identifier of the event.
     *
     * @param ID The new unique identifier
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName The new event name
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Sets the location of the event.
     *
     * @param location The new event location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the date of the event.
     *
     * @param date The new event date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the time of the event.
     *
     * @param time The new event time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Sets the description of the event.
     *
     * @param description The new event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the maximum capacity for the event.
     *
     * @param capacity The limit chosen by the organizer
     */
    public void setCapacity(String capacity) { this.capacity = capacity; }

    /**
     * Sets the resource ID for the event poster.
     *
     * @param eventPosterURL The new resource ID
     */
    public void setEventPosterURL(String eventPosterURL) {
        this.eventPosterURL = eventPosterURL;
    }

    /**
     * Sets the check-in ID for the event.
     *
     * @param checkInID The new check-in ID
     */
    public void setCheckInID(String checkInID) {this.checkInID = checkInID;}

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
        dest.writeString(eventName);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(description);
        dest.writeString(capacity);
        dest.writeString(eventPosterURL);
        dest.writeString(checkInID);
    }
}