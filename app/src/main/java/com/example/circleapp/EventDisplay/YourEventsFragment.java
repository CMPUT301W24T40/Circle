package com.example.circleapp.EventDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.QRCode.ScanQRActivity;
import com.example.circleapp.R;

import java.util.ArrayList;
import java.util.List;

public class YourEventsFragment extends Fragment {
    RecyclerView recyclerView;
    Button scanButton;
    ArrayList<Event> events;
    EventAdapter adapter;
    EventAdapter.OnItemClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_events, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        scanButton = rootView.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), ScanQRActivity.class);
            startActivity(intent);
        });

        events = getEvents();

        // Set up the click listener
        listener = this::eventClicked;

        // Pass the click listener to the adapter
        adapter = new EventAdapter(events, listener);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    // handle item clicks
    private void eventClicked(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private ArrayList<Event> getEvents() {
        // Use dummy data for now
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("321", "Event 1", "Location 1", "Date 1", "Time 1", "Description 1"));
        return events;
    }

}
