package com.example.circleapp.EventDisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Announcement;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.example.circleapp.TempUserInfoActivity;

import java.util.ArrayList;

/**
 * This class is used to display event details for events launched from the Browse page.
 */
public class BrowseEventsDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Button registerButton;
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

    /**
     * When this Activity is created, a user can view the details of the event they clicked on
     * from the BrowseEventsFragment. Within this page, there will be a list of announcements for the
     * event. There is also a back button that will send the user back to the fragment this activity
     * was launched from.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle)
     * @see BrowseEventsFragment
     * @see AnnouncementAdapter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events_details);

        event = getIntent().getParcelableExtra("event");
        assert event != null;
        String eventId = event.getID();

        TextView noAnnouncementsTextView = findViewById(R.id.no_announcements_textview);
        listView = findViewById(R.id.announcement_listview);

        announcementsList = new ArrayList<>();
        firebaseManager.loadAnnouncements(eventId, announcements -> {
            announcementsList.clear();
            announcementsList.addAll(announcements);

            announcementAdapter = new AnnouncementAdapter(BrowseEventsDetailsActivity.this, announcementsList);
            listView.setAdapter(announcementAdapter);

            if (announcementsList.isEmpty()) {
                noAnnouncementsTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            else {
                noAnnouncementsTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

        listView.setAdapter(announcementAdapter);

        eventNameTextView = findViewById(R.id.event_details_name);
        eventLocationTextView = findViewById(R.id.event_details_location);
        eventDateTextView = findViewById(R.id.event_details_date);
        eventTimeTextView = findViewById(R.id.event_details_time);
        eventDescriptionTextView = findViewById(R.id.event_details_description);
        eventCapacityTextView = findViewById(R.id.event_details_capacity);
        eventPosterImageView = findViewById(R.id.event_details_poster);

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

        String eventPosterURL = event.getEventPosterURL();
        if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
            Glide.with(this).load(eventPosterURL).apply(new RequestOptions().placeholder(R.drawable.no_poster)).into(eventPosterImageView);
        }
        else {
            Glide.with(this).load(R.drawable.no_poster).into(eventPosterImageView);
        }

        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> firebaseManager.checkUserExists(exists -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowseEventsDetailsActivity.this);
            if (exists) {
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to register?");
                builder.setPositiveButton("Yes", (dialog, which) -> firebaseManager.registerEvent(event, this));
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                builder.setTitle("Details needed");
                builder.setMessage("Before registering for an event, we need some details from you");
                builder.setPositiveButton("Let's go!", (dialog, which) -> {
                    Intent intent = new Intent(this, TempUserInfoActivity.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }));
    }
}