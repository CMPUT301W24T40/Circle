package com.example.circleapp.EventDisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.R;

import java.util.ArrayList;

/**
 * Adapter for displaying events in a ListView.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor for EventAdapter.
     *
     * @param context The context
     * @param events  The list of events
     * @see BrowseEventsFragment
     * @see YourEventsFragment
     */
    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
        }

        Event event = getItem(position);

        TextView eventName = convertView.findViewById(R.id.event_title);
        TextView eventDescription = convertView.findViewById(R.id.event_description);
        TextView eventDetails = convertView.findViewById(R.id.details_title);
        ImageView eventPoster = convertView.findViewById(R.id.event_poster);

        if (event != null) {
            eventName.setText(event.getEventName());
            eventDescription.setText(event.getDescription());
            // Set other event details if needed
        }

        return convertView;
    }
}