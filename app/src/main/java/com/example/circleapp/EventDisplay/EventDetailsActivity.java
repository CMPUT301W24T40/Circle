package com.example.circleapp.EventDisplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.QRCode.GenerateQRActivity;
import com.example.circleapp.R;
import com.example.circleapp.TempUserInfoActivity;
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
    @SuppressLint("SetTextI18n")
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

        // Initialize buttons
        backButton = findViewById(R.id.back_button);
        generateQRButton = findViewById(R.id.generate_qr_button);
        registerButton = findViewById(R.id.register_button);
        guestlistButton = findViewById(R.id.guestlist_button);

        // Set visibility of register, guest list, and QR buttons based on source
        String source = getIntent().getStringExtra("source");

        registerButton.setVisibility(View.GONE);
        if ("BrowseEventsFragment".equals(source)) {
            registerButton.setVisibility(View.VISIBLE);
        }

        guestlistButton.setVisibility(View.GONE);
        generateQRButton.setVisibility(View.GONE);
        if ("CreatedEventsFragment".equals(source)) {
            guestlistButton.setVisibility(View.VISIBLE);
            generateQRButton.setVisibility(View.VISIBLE);
        }

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Generate QR details button click listener
        generateQRButton.setOnClickListener(v -> {
            // Create an AlertDialog to ask the user what type of QR code they want to generate
            //TODO: Customize alertdialog box
            new AlertDialog.Builder(this)
                    .setTitle("QR Code Type")
                    .setMessage("What type of QR code would you like to generate?")
                    .setPositiveButton("Check-in", (dialog, which) -> startGenerateQRActivity("check-in"))
                    .setNegativeButton("Details", (dialog, which) -> startGenerateQRActivity("details"))
                    .show();
        });

        // Register button click listener
        registerButton.setOnClickListener(v -> firebaseManager.checkUserExists(exists -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
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
                builder.setMessage("Before creating an event, we need some details from you");
                builder.setPositiveButton("Let's go!", (dialog, which) -> {
                    Intent intent = new Intent(this, TempUserInfoActivity.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }));

        guestlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestlistActivity.class);
            intent.putExtra("source", "GuestlistActivity");
            intent.putExtra("event", event);
            startActivity(intent);
        });
    }

    // New method to start GenerateQRActivity with the given qrType
    private void startGenerateQRActivity(String qrType) {
        Intent intent = new Intent(this, GenerateQRActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("qrType", qrType);
        startActivity(intent);
    }
}