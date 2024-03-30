package com.example.circleapp.UserDisplay;

import com.example.circleapp.BaseObjects.Attendee;

import java.util.ArrayList;

public interface CheckedInAttendeesCallback {
    void onAttendeesReceived(ArrayList<Attendee> attendees);
}
