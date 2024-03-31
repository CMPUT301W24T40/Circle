package com.example.circleapp.UserDisplay;

import com.example.circleapp.BaseObjects.Attendee;

import java.util.ArrayList;

/**
 * An interface defining a callback for receiving a list of checked-in attendees.
 */
public interface CheckedInAttendeesCallback {
    /**
     * Called when the list of checked-in attendees is received.
     *
     * @param attendees An ArrayList containing the checked-in attendees.
     */
    void onAttendeesReceived(ArrayList<Attendee> attendees);
}
