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
import com.bumptech.glide.Glide;

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
        TextView eventDate = convertView.findViewById(R.id.date_display);
        TextView eventTime = convertView.findViewById(R.id.time_display);
        ImageView eventPoster = convertView.findViewById(R.id.event_poster);

        if (event != null) {
            eventName.setText(event.getEventName());
            eventDescription.setText(event.getDescription());
            eventDate.setText(event.getDate());
            eventTime.setText(event.getTime());

            if (event.getEventPosterURL() != null && !event.getEventPosterURL().isEmpty()) {
                Glide.with(getContext())
                        .load(event.getEventPosterURL())
                        .into(eventPoster);
            }
            else {
                eventPoster.setImageResource(R.drawable.no_poster);
            }
        }

        return convertView;
    }
}