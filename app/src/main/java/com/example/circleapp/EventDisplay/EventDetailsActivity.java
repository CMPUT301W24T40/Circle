package com.example.circleapp.EventDisplay;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.QRCode.GenerateQRActivity;
import com.example.circleapp.R;
import com.example.circleapp.UserDisplay.GuestlistActivity;

/**
 * This class is used to display event details.
 */
public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance
    Button backButton;
    Button generateQRButton;
    Button registerButton;
    Button guestlistButton;
    TextView eventNameTextView;
    TextView eventLocationTextView;
    TextView eventDateTextView;
    TextView eventTimeTextView;
    TextView eventDescriptionTextView;
    TextView eventCapacityTextView;
    ImageView eventPosterImageView;

    /**
     * When this Activity is created, a user can view the details of the event they clicked on
     * (clicked on from either BrowseEventsFragment or YourEventsFragment). Within this page, there
     * will be a button to generate a QR code that the user can send to other users to share the event
     * details. If the user clicked on the event from the BrowseEventsFragment, there will also be a
     * button to register for the event. After confirmation of registration, this event will be added
     * to the user's registeredEvents sub-collection in Firestore (sub-collection of user document), and
     * will be displayed on the YourEventsFragment. There is also a back button that will send the user
     * back to the fragment this activity was launched from.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle)
     * @see BrowseEventsFragment
     * @see YourEventsFragment
     * @see GenerateQRActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        event = getIntent().getParcelableExtra("event");

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
        // Set event poster image if needed (not provided in the Event class)

        // Initialize buttons
        backButton = findViewById(R.id.back_button);
        generateQRButton = findViewById(R.id.generate_qr_button);
        registerButton = findViewById(R.id.register_button);
        guestlistButton = findViewById(R.id.guestlist_button);

        // Set visibility of register and guest list buttons based on source
        String source = getIntent().getStringExtra("source");

        registerButton.setVisibility(View.GONE);
        if ("BrowseEventsFragment".equals(source)) {
            registerButton.setVisibility(View.VISIBLE);
        }

        Log.d("WhatFragment", "Source: " + source);
        guestlistButton.setVisibility(View.GONE);
        if ("CreatedEventsFragment".equals(source)) {
            guestlistButton.setVisibility(View.VISIBLE);
        }

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Generate QR button click listener
        generateQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateQRActivity.class);
            intent.putExtra("event", event);
            intent.putExtra("qrType", "details");
            startActivity(intent);
        });

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to register?");
            builder.setPositiveButton("Yes", (dialog, which) -> firebaseManager.registerEvent(event, this));
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        guestlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestlistActivity.class);
            intent.putExtra("source", "GuestlistActivity");
            intent.putExtra("event", event);
            startActivity(intent);
        });
    }
}