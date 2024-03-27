package com.example.circleapp.BaseObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Announcement implements Parcelable {
    private String announcementID;
    private String title;
    private String content;
    private String timestamp;

    // Constructors
    public Announcement(String announcementID, String title, String content, String timestamp) {
        this.announcementID = announcementID;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Announcement() {}

    public Announcement(Parcel in) {
        announcementID = in.readString();
        title = in.readString();
        content = in.readString();
        timestamp = in.readString();
    }

    // Parcelable creator
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

    public String getAnnouncementID() {
        return announcementID;
    }

    public void setAnnouncementID(String announcementID) {
        this.announcementID = announcementID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    // Generate timestamp
    private String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(announcementID);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(timestamp);
    }
}
