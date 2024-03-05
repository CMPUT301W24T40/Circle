package com.example.circleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YourEventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_events, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get current attendee and their signed-up events
        Attendee currentAttendee = getCurrentAttendee();

        // Display signed-up events
        if (currentAttendee.getEvents() != null) {
            List<Event> signedUpEvents = currentAttendee.getEvents();
            EventAdapter adapter = new EventAdapter(signedUpEvents);
            recyclerView.setAdapter(adapter);
        }

        Button scanButton = rootView.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), com.example.circleapp.ScanQRActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    // Method to retrieve the current attendee
    private Attendee getCurrentAttendee() {
        // implement Firebase authentication and database access here
        return new Attendee("John Doe", "john@example.com"); // dummy return
    }
}