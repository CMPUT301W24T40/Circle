package com.example.circleapp.EventDisplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.Firebase.ImageManager;
import com.example.circleapp.R;

import java.util.Calendar;

/**
 * This class is used for when a user wants to create an event.
 */
public class CreateEventActivity extends AppCompatActivity {
    EditText eventNameEditText;
    EditText locationEditText;
    EditText descriptionEditText;
    ImageView eventPoster;
    Button confirmButton;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance
    ImageManager imageManager;
    TextView datePicker;
    TextView timePicker;
    NumberPicker capacityPicker;

    /**
     * When this Activity is created, a user can input details to create an Event. Details include
     * event name, location, date, description, # of guests allowed, and an event poster. After
     * confirmation, Event is created and added to Firestore database in the "events" collection.
     * The event is put into a bundle and sent back to the fragment (BrowseEventsFragment) that
     * started the activity.
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

        eventNameEditText = findViewById(R.id.eventName_edit);
        locationEditText = findViewById(R.id.location_edit);
        descriptionEditText = findViewById(R.id.description_edit);
        eventPoster = findViewById(R.id.eventPoster_edit);
        confirmButton = findViewById(R.id.create_event_button);

        datePicker = findViewById(R.id.date_edit_picker);
        timePicker = findViewById(R.id.time_edit_picker);

        capacityPicker = findViewById(R.id.capacity_picker);

        capacityPicker.setMinValue(1);
        capacityPicker.setMaxValue(1000);

        datePicker.setOnClickListener(v -> openDateDialog());

        timePicker.setOnClickListener(v -> openTimeDialog());

        imageManager = new ImageManager(this, eventPoster);
        eventPoster.setOnClickListener(v -> imageManager.selectPosterImage());

        confirmButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String location = locationEditText.getText().toString();

            String date = datePicker.getText().toString();
            String time = timePicker.getText().toString();
            String description = descriptionEditText.getText().toString();

            String capacity = String.valueOf(capacityPicker.getValue());

            String ID = firebaseManager.generateRandomID();

            if (eventName.isEmpty() ||
                    location.isEmpty() ||
                    date.equals("YYYY/MM/DD") ||
                    time.equals("XX:XX") ||
                    description.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
                builder.setTitle("Wait a minute!")
                        .setMessage("Please fill in all event details before creating an event.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }

            Event event = new Event(ID, eventName, location, date, time, description);
            if (capacity.isEmpty()) { event.setCapacity("-1"); }
            else { event.setCapacity(capacity); }

            if (imageManager.hasImage()) {
                imageManager.uploadPosterImage(downloadURL -> {
                    event.setEventPosterURL(downloadURL);
                    firebaseManager.createEvent(event);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", event);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);

                    setResult(Activity.RESULT_OK, intent);

                    finish();
                });
            }
            else {
                String defaultImageURL = getString(R.string.Default_event_poster_URL);
                event.setEventPosterURL(defaultImageURL);
                firebaseManager.createEvent(event);

                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);
                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });
    }

    /**
     * Handles the result of an activity launched for a result.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The data returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method is used to choose and display the date the user wants for their event.
     */
    private void openDateDialog() {
        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH);
        int currDay = c.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("DefaultLocale") DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                datePicker.setText(String.format("%s/%02d/%02d", year, month + 1, dayOfMonth)), currYear, currMonth, currDay);
        dialog.show();
    }

    /**
     * This method is used to choose and display the time the user wants for their event.
     */
    private void openTimeDialog() {
        @SuppressLint("DefaultLocale") TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) ->
                timePicker.setText(String.format("%02d:%02d", hourOfDay, minute)), 12, 00, true);
        dialog.show();
    }
}