package com.example.circleapp.UserDisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.R;

import java.util.ArrayList;

/**
 * Adapter for displaying attendees in a ListView.
 */
public class AttendeeAdapter extends ArrayAdapter<Attendee> {

    /**
     * Constructor for AttendeeAdapter.
     *
     * @param context The context
     * @param attendees  The list of attendees
     * @see GuestlistActivity
     */
    public AttendeeAdapter(Context context, ArrayList<Attendee> attendees) {
        super(context, 0, attendees);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attendee_item, parent, false);
        }

        Attendee attendee = getItem(position);

        TextView attendeeFirstName = convertView.findViewById(R.id.attendee_fname_title);
        TextView attendeeLastName = convertView.findViewById(R.id.attendee_lname_title);

        if (attendee != null) {
            attendeeFirstName.setText(attendee.getFirstName());
            attendeeLastName.setText(attendee.getLastName());
            // Set other attendee details if needed
        }

        return convertView;
    }

}