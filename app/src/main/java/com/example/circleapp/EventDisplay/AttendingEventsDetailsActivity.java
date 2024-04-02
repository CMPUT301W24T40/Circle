package com.example.circleapp.EventDisplay;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Announcement;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;

import java.util.ArrayList;

public class AttendingEventsDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance
    TextView eventNameTextView;
    TextView eventLocationTextView;
    TextView eventDateTextView;
    TextView eventTimeTextView;
    TextView eventDescriptionTextView;
    TextView eventCapacityTextView;
    ImageView eventPosterImageView;
    private ArrayList<Announcement> announcementsList;
    private AnnouncementAdapter announcementAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_events_details);

        event = getIntent().getParcelableExtra("event");
        assert event != null;
        String eventId = event.getID(); // Use getID() to access the event's ID

        TextView noAnnouncementsTextView = findViewById(R.id.no_announcements_textview);
        listView = findViewById(R.id.announcement_listview);

        // Initialize announcements list
        announcementsList = new ArrayList<>();
        // Load announcements from Firebase and then set up the ListView and its adapter
        firebaseManager.loadAnnouncements(eventId, announcements -> {
            announcementsList.clear();
            announcementsList.addAll(announcements);

            announcementAdapter = new AnnouncementAdapter(AttendingEventsDetailsActivity.this, announcementsList);
            listView.setAdapter(announcementAdapter);

            // After loading announcements, check if the list is empty
            if (announcementsList.isEmpty()) {
                noAnnouncementsTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noAnnouncementsTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });
        //updates list immediately after adding announcement (from organizer's view)
        listView.setAdapter(announcementAdapter);

        // Initialize views
        eventNameTextView = findViewById(R.id.event_details_name);
        eventLocationTextView = findViewById(R.id.event_details_location);
        eventDateTextView = findViewById(R.id.event_details_date);
        eventTimeTextView = findViewById(R.id.event_details_time);
        eventDescriptionTextView = findViewById(R.id.event_details_description);
        eventCapacityTextView = findViewById(R.id.event_details_capacity);
        eventPosterImageView = findViewById(R.id.event_details_poster);

        // Set event details
        eventNameTextView.setText(event.getEventName());
        eventLocationTextView.setText(event.getLocation());
        eventDateTextView.setText(event.getDate());
        eventTimeTextView.setText(event.getTime());
        eventDescriptionTextView.setText(event.getDescription());

        if (event.getCapacity().equalsIgnoreCase("-1")) {
            eventCapacityTextView.setText("Not specified");
        }
        else {
            eventCapacityTextView.setText(event.getCapacity());
        }


        // Load event poster image
        String eventPosterURL = event.getEventPosterURL();
        if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
            Glide.with(this)
                    .load(eventPosterURL)
                    .apply(new RequestOptions().placeholder(R.drawable.no_poster))
                    .into(eventPosterImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.no_poster)
                    .into(eventPosterImageView);
        }


        // Unregister from an event
        Button unregisterButton = findViewById(R.id.register_button);

        unregisterButton.setText("Unregister");
        unregisterButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AttendingEventsDetailsActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to unregister?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                firebaseManager.unregisterEvent(event, this);
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}


