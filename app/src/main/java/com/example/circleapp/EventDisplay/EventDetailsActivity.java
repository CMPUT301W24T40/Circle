package com.example.circleapp.EventDisplay;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.QRCode.GenerateQRActivity;
import com.example.circleapp.R;

/**
 * This class is used to display event details.
 */
public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance
    Button backButton;
    Button generateQRButton;
    Button registerButton;
    TextView eventNameTextView;
    TextView eventLocationTextView;
    TextView eventDateTextView;
    TextView eventTimeTextView;
    TextView eventDescriptionTextView;
    ImageView eventPosterImageView;

    /**
     * When this Activity is created, a user can view the details of the event they clicked on
     * (clicked on from either BrowseEventsFragment or YourEventsFragment). Within this page, there
     * will be a button to generate a QR code that the user can send to other users to share the event
     * details. If the user clicked on the event from the BrowseEventsFragment, there will also be a
     * button to register for the event. After confirmation of registration, this event will be added
     * to the user's registeredEvents subcollection in Firestore (subcollection of user document), and
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
        setContentView(R.layout.event_details_activity);
        event = getIntent().getParcelableExtra("event");

        // Initialize views
        eventNameTextView = findViewById(R.id.event_details_name);
        eventLocationTextView = findViewById(R.id.event_details_location);
        eventDateTextView = findViewById(R.id.event_details_date);
        eventTimeTextView = findViewById(R.id.event_details_time);
        eventDescriptionTextView = findViewById(R.id.event_details_description);
        eventPosterImageView = findViewById(R.id.event_details_poster);

        // Set event details
        eventNameTextView.setText(event.getEventName());
        eventLocationTextView.setText(event.getLocation());
        eventDateTextView.setText(event.getDate());
        eventTimeTextView.setText(event.getTime());
        eventDescriptionTextView.setText(event.getDescription());
        // Set event poster image if needed (not provided in the Event class)

        // Initialize buttons
        backButton = findViewById(R.id.back_button);
        generateQRButton = findViewById(R.id.generate_qr_button);
        registerButton = findViewById(R.id.register_button);

        // Set visibility of register button based on source
        registerButton.setVisibility(View.GONE);
        String source = getIntent().getStringExtra("source");
        if ("BrowseEventsFragment".equals(source)) {
            registerButton.setVisibility(View.VISIBLE);
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
            builder.setPositiveButton("Yes", (dialog, which) -> firebaseManager.registerEvent(event));
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}