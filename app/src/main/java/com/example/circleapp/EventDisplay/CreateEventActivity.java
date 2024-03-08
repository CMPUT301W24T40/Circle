package com.example.circleapp.EventDisplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;

/**
 * This class is used for when a user wants to create an event.
 */
public class CreateEventActivity extends AppCompatActivity {
    EditText eventNameEditText;
    EditText locationEditText;
    EditText dateEditText;
    EditText descriptionEditText;
    ImageView eventPoster;
    Button confirmButton;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance

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
        descriptionEditText = findViewById(R.id.description_edit);
        eventPoster = findViewById(R.id.eventPoster_edit);
        confirmButton = findViewById(R.id.create_event_button);

        // Confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String ID = firebaseManager.generateRandomID();

            Event event = new Event(ID, eventName, location, date, "7:30", description);

            firebaseManager.addNewEvent(event);

            // Send result back to the caller activity
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", event);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            setResult(Activity.RESULT_OK, intent);

            finish();
        });
    }
}