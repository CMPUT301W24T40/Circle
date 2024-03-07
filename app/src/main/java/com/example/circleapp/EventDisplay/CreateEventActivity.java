package com.example.circleapp.EventDisplay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;

public class CreateEventActivity extends AppCompatActivity {
    EditText eventNameEditText;
    EditText locationEditText;
    EditText dateEditText;
    EditText timeEditText;
    EditText descriptionEditText;
    ImageView eventPoster;
    Button confirmButton;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventNameEditText = findViewById(R.id.eventName_edit);
        locationEditText = findViewById(R.id.location_edit);
        dateEditText = findViewById(R.id.date_edit);
        //timeEditText = findViewById(R.id.time_edit);
        descriptionEditText = findViewById(R.id.description_edit);
        eventPoster = findViewById((R.id.eventPoster_edit));
        confirmButton = findViewById(R.id.create_event_button);

        imagePickLauncher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null) {
                            selectedImageUri = data.getData();
                            Glide.with(this).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(eventPoster);
                        }
                    }
                }
        );

        confirmButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String ID = firebaseManager.generateRandomID();

            Event event = new Event(ID, eventName, location, date, time, description);

            firebaseManager.addNewEvent(event);

            Bundle bundle = new Bundle();
            bundle.putParcelable("event", event);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            setResult(Activity.RESULT_OK, intent);

            finish();
        });
    }
}