package com.example.circleapp.UserDisplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class displays a map view with markers representing checked-in attendees of an event.
 */
public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    Button backButton;
    GoogleMap attendeeMap;
    Event event;
    FirebaseManager firebaseManager;
    ArrayList<Attendee> checkedInAttendees;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);

        firebaseManager = FirebaseManager.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        event = getIntent().getParcelableExtra("event");
        firebaseManager.getCheckedInAttendees(Objects.requireNonNull(event).getID(), attendees -> {
            checkedInAttendees = attendees;
            Log.d("mom_attendees", checkedInAttendees.toString());

            if (checkedInAttendees.isEmpty()) {
                Toast.makeText(MapViewActivity.this, "No attendees have checked in yet!", Toast.LENGTH_LONG).show();
            }
            else if (attendeeMap != null) { populateMap(); }
        });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Called when the map is ready to be used.
     *
     * @param googleMap The GoogleMap object representing the map.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        attendeeMap = googleMap;
        if (checkedInAttendees != null && !checkedInAttendees.isEmpty()) { populateMap(); }
    }

    /**
     * Populates the map with markers representing checked-in attendees.
     */
    private void populateMap() {
        for (Attendee attendee : checkedInAttendees) {
            Log.d("oo", attendee.getFirstName());
            if (attendee.getLocationLatitude() != null && attendee.getLocationLongitude() != null) {
                Log.d("valid", attendee.getFirstName());
                LatLng location = new LatLng(attendee.getLocationLatitude(), attendee.getLocationLongitude());
                attendeeMap.addMarker(new MarkerOptions().position(location).title(attendee.getFirstName()));
                attendeeMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        }
    }
}