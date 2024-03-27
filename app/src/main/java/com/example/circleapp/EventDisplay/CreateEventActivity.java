package com.example.circleapp.EventDisplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.Firebase.ImageManager;
import com.example.circleapp.R;

/**
 * This class is used for when a user wants to create an event.
 */
public class CreateEventActivity extends AppCompatActivity {
    EditText eventNameEditText;
    EditText locationEditText;
    EditText dateEditText;
    EditText timeEditText;
    EditText descriptionEditText;
    EditText capacityEditText;
    ImageView eventPoster;
    Button confirmButton;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance
    ImageManager imageManager;

    /**
     * When this Activity is created, a user can input details to create an Event. Details include
     * event name, location, date, and description. After confirmation, Event is created and added to
     * Firestore database in the "events" collection. The event is put into a bundle and sent back to
     * the fragment (BrowseEventsFragment) that started the activity
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle)
     * @see BrowseEventsFragment
     * @see FirebaseManager
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize views
        eventNameEditText = findViewById(R.id.eventName_edit);
        locationEditText = findViewById(R.id.location_edit);
        dateEditText = findViewById(R.id.date_edit);
        timeEditText = findViewById(R.id.time_edit);
        descriptionEditText = findViewById(R.id.description_edit);
        capacityEditText = findViewById(R.id.capacity_edit);
        eventPoster = findViewById(R.id.eventPoster_edit);
        confirmButton = findViewById(R.id.create_event_button);

        imageManager = new ImageManager(this, eventPoster);
        // click listener for eventPoster
        eventPoster.setOnClickListener(v -> imageManager.selectImage());

        // Confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String capacity = capacityEditText.getText().toString();
            String ID = firebaseManager.generateRandomID();

            Event event = new Event(ID, eventName, location, date, time, description);
            if (capacity.isEmpty()) { event.setCapacity("-1"); }
            else { event.setCapacity(capacity); }

            if (imageManager.hasImage()) {
                imageManager.uploadPosterImage(downloadURL -> {
                    event.setEventPosterURL(downloadURL);
                    firebaseManager.createEvent(event);

                    // Send result back to the caller activity
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", event);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);

                    setResult(Activity.RESULT_OK, intent);

                    finish();
                });
            } else {
                // Set default image URL if no image is selected
                String defaultImageURL = getString(R.string.Default_event_poster_URL);
                event.setEventPosterURL(defaultImageURL);
                firebaseManager.createEvent(event);

                // Send result back to the caller activity
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);
                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageManager.onActivityResult(requestCode, resultCode, data);
    }
}