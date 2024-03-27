package com.example.circleapp.UserDisplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.circleapp.BaseObjects.Attendee;
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
    ArrayList<Attendee> attendees;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        attendees = getIntent().getParcelableArrayListExtra("attendees");
        assert attendees != null;
        for (Attendee attendee : attendees) {
            assert attendee != null;
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

        LatLng tempCity = new LatLng(53.5461, -113.4937);
        attendeeMap.addMarker(new MarkerOptions().position(tempCity).title("Edmonton"));
        attendeeMap.moveCamera(CameraUpdateFactory.newLatLng(tempCity));
    }

}