package com.example.circleapp.UserDisplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    Button backButton;
    GoogleMap attendeeMap;
    Event event;
    FirebaseManager firebaseManager;
    ArrayList<Attendee> checkedInAttendees;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);

        firebaseManager = FirebaseManager.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        event = getIntent().getParcelableExtra("event");

        assert event != null;
        checkedInAttendees = firebaseManager.getCheckedInAttendees(event.getID());
        Log.d("mom_attendees", checkedInAttendees.toString());

        if (checkedInAttendees.isEmpty()) {
            Toast.makeText(this, "No attendees have checked in yet!", Toast.LENGTH_LONG).show();
        }

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        attendeeMap = googleMap;

        for (Attendee attendee : checkedInAttendees) {
            assert attendee != null;

            LatLng location = new LatLng(attendee.getLocation().getLatitude(), attendee.getLocation().getLongitude());
            attendeeMap.addMarker(new MarkerOptions().position(location).title(attendee.getFirstName()));
            attendeeMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

}