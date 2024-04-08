package com.example.circleapp.BaseObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents an announcement object.
 */
public class Announcement implements Parcelable {
    private String ID;
    private String title;
    private String content;
    private String timestamp;

    // Constructors

    /**
     * Constructs an Announcement object with no parameters.
     */
    public Announcement() {}

    /**
     * Constructs an Announcement object from a Parcel.
     *
     * @param in The Parcel from which to read the Announcement object.
     */
    protected Announcement(Parcel in) {
        ID = in.readString();
        title = in.readString();
        content = in.readString();
        timestamp = in.readString();
    }

    /**
     * Creator constant for Parcelable implementation.
     */
    public static final Creator<Announcement> CREATOR = new Creator<Announcement>() {
        @Override
        public Announcement createFromParcel(Parcel in) {
            return new Announcement(in);
        }

        @Override
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };

    // Getters and setters

    /**
     * Gets the ID of the announcement.
     *
     * @return The ID of the announcement.
     */
    public String getAnnouncementID() {
        return ID;
    }

    /**
     * Gets the content of the announcement.
     *
     * @return The content of the announcement.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp of the announcement.
     *
     * @return The timestamp of the announcement.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the ID of the announcement.
     *
     * @param ID The ID to set.
     */
    public void setAnnouncementID(String ID) {
        this.ID = ID;
    }

    /**
     * Sets the title of the announcement.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the content of the announcement.
     *
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets the timestamp of the announcement.
     *
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Parcelable methods

    /**
     * Returns a bitmask indicating the set of special object types contained in this Parcelable instance.
     *
     * @return Always returns 0 as this Parcelable object doesn't contain any special object types.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten Announcement object into a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written. May be 0 or {@link android.os.Parcelable#PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(timestamp);
    }
}