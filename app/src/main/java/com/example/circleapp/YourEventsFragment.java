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

import java.util.ArrayList;
import java.util.List;

public class YourEventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_events, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button scanButton = rootView.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), com.example.circleapp.ScanQRActivity.class);
            startActivity(intent);
        });

        List<Event> events = getEvents();

        // Set up the click listener
        EventAdapter.OnItemClickListener listener = event -> eventClicked(event);

        // Pass the click listener to the adapter
        EventAdapter adapter = new EventAdapter(events, listener);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    // handle item clicks
    private void eventClicked(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private List<Event> getEvents() {
        // Use dummy data for now
        List<Event> events = new ArrayList<>();
        events.add(new Event("Event 1", "Location 1", "Date 1", "Time 1", "Description 1", R.drawable.event_image_dummy ));
        return events;
    }

}
