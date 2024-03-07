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
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;

import java.util.ArrayList;
import java.util.List;

public class BrowseEventsFragment extends Fragment {
    Button addEvent;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_events, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Event> events = getEvents();

        //null value disables the clickListener for the Browse page
        EventAdapter adapter = new EventAdapter(events, null);
        recyclerView.setAdapter(adapter);

        addEvent = rootView.findViewById(R.id.add_event_button);

        addEvent.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), CreateEventActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private List<Event> getEvents() {
        // Use dummy data for now
        List<Event> events = new ArrayList<>();
        events.add(new Event("123", "Event 1", "Location 1", "Date 1", "Time 1", "Description 1"));
        events.add(new Event("456", "Event 2", "Location 2", "Date 2", "Time 2", "Description 2"));
        events.add(new Event("789", "Event 3", "Location 3", "Date 3", "Time 3", "Description 3"));
        return events;
    }
}