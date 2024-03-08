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

public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Button backButton;
    Button generateQRButton;
    Button registerButton;
    TextView eventNameTextView;
    TextView eventLocationTextView;
    TextView eventDateTimeTextView;
    TextView eventDescriptionTextView;
    ImageView eventPosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);
        event = getIntent().getParcelableExtra("event");

        eventNameTextView = findViewById(R.id.event_details_name);
        eventLocationTextView = findViewById(R.id.event_details_location);
        eventDateTimeTextView = findViewById(R.id.event_details_date_time);
        eventDescriptionTextView = findViewById(R.id.event_details_description);
        eventPosterImageView = findViewById(R.id.event_details_poster);

        eventNameTextView.setText(event.getEventName());
        eventLocationTextView.setText(event.getLocation());
        eventDateTimeTextView.setText(event.getDate() + " " + event.getTime());
        eventDescriptionTextView.setText(event.getDescription());
        eventPosterImageView.setImageResource(event.getEventPoster());

        backButton = findViewById(R.id.back_button);
        generateQRButton = findViewById(R.id.generate_qr_button);
        registerButton = findViewById(R.id.register_button);

        registerButton.setVisibility(View.GONE);
        String source = getIntent().getStringExtra("source");
        if ("BrowseEventsFragment".equals(source)) {
            registerButton.setVisibility(View.VISIBLE);
        }

        backButton.setOnClickListener(v -> finish());

        generateQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GenerateQRActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

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