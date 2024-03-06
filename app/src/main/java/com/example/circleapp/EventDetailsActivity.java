package com.example.circleapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//PAGE FOR VIEWING DETAILS OF AN EVENT, NOT YET IMPLEMENTED
public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);

        Event event = (Event) getIntent().getSerializableExtra("event");

        TextView eventNameTextView = findViewById(R.id.event_details_name);
        TextView eventLocationTextView = findViewById(R.id.event_details_location);
        TextView eventDateTimeTextView = findViewById(R.id.event_details_date_time);
        TextView eventDescriptionTextView = findViewById(R.id.event_details_description);
        ImageView eventPosterImageView = findViewById(R.id.event_details_poster);

        eventNameTextView.setText(event.getEventName());
        eventLocationTextView.setText(event.getLocation());
        eventDateTimeTextView.setText(event.getDate() + " " + event.getTime());
        eventDescriptionTextView.setText(event.getDescription());
        eventPosterImageView.setImageResource(event.getEventPoster());
    }
}