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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_events, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button scanButton = rootView.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), ScanQRActivity.class);
            startActivity(intent);
        });

        List<Event> events = getEvents();

        // Set up the click listener
        EventAdapter.OnItemClickListener listener = this::eventClicked;

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
        events.add(new Event("321", "Event 1", "Location 1", "Date 1", "Time 1", "Description 1"));
        return events;
    }

}
