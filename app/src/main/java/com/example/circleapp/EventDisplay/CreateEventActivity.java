package com.example.circleapp.EventDisplay;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
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

    // for new date and time picker
    TextView datePicker;
    TextView timePicker;

    // new for capacity
    NumberPicker capacityPicker;

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
        descriptionEditText = findViewById(R.id.description_edit);
        eventPoster = findViewById(R.id.eventPoster_edit);
        confirmButton = findViewById(R.id.create_event_button);

        // new date and time picker
        datePicker = findViewById(R.id.date_edit_picker);
        timePicker = findViewById(R.id.time_edit_picker);

        // new capacity picker
        capacityPicker = findViewById(R.id.capacity_picker);

        capacityPicker.setMinValue(1);
        capacityPicker.setMaxValue(1000);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });

        imageManager = new ImageManager(this, eventPoster);
        // click listener for eventPoster
        eventPoster.setOnClickListener(v -> imageManager.selectImage());

        // Confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String location = locationEditText.getText().toString();

            // new date and time
            String date = datePicker.getText().toString();
            String time = timePicker.getText().toString();
            String description = descriptionEditText.getText().toString();

            // new capacity
            String capacity = String.valueOf(capacityPicker.getValue());

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

    /**
     * This method is used to choose and display the date the user wants for their event.
     */
    private void openDateDialog() {
        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH);
        int currDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                datePicker.setText(String.format("%s/%02d/%02d", year, month + 1, dayOfMonth));
            }
        }, currYear, currMonth, currDay);
        dialog.show();
    }

    /**
     * This method is used to choose and display the time the user wants for their event.
     */
    private void openTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timePicker.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, 12, 00, true);
        dialog.show();
    }
}