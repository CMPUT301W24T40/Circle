package com.example.circleapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BrowseEventsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_events, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Event> events = getEvents();

        EventAdapter adapter = new EventAdapter(events);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    //FOR TESTING PURPOSES, IMPLEMENT LATER
    private List<Event> getEvents() {
        // Use dummy data for now
        List<Event> events = new ArrayList<>();
        events.add(new Event("Event 1", "Location 1", "Date 1", "Time 1", "Description 1", R.drawable.event_image_dummy ));
        events.add(new Event("Event 2", "Location 2", "Date 2", "Time 2", "Description 2", R.drawable.event_image_dummy));
        events.add(new Event("Event 3", "Location 3", "Date 3", "Time 3", "Description 3", R.drawable.event_image_dummy));
        events.add(new Event("Event 4", "Location 4", "Date 4", "Time 4", "Description 4", R.drawable.event_image_dummy));
        events.add(new Event("Event 5", "Location 5", "Date 5", "Time 5", "Description 5", R.drawable.event_image_dummy));
        events.add(new Event("Event 6", "Location 6", "Date 6", "Time 6", "Description 6", R.drawable.event_image_dummy));
        events.add(new Event("Event 7", "Location 7", "Date 7", "Time 7", "Description 7", R.drawable.event_image_dummy));
        events.add(new Event("Event 8", "Location 8", "Date 8", "Time 8", "Description 8", R.drawable.event_image_dummy));
        events.add(new Event("Event 9", "Location 9", "Date 9", "Time 9", "Description 9", R.drawable.event_image_dummy));
        events.add(new Event("Event 10", "Location 10", "Date 10", "Time 10", "Description 10", R.drawable.event_image_dummy));
        events.add(new Event("Event 11", "Location 11", "Date 11", "Time 11", "Description 11", R.drawable.event_image_dummy));
        events.add(new Event("Event 12", "Location 12", "Date 12", "Time 12", "Description 12", R.drawable.event_image_dummy));
        events.add(new Event("Event 13", "Location 13", "Date 13", "Time 13", "Description 13", R.drawable.event_image_dummy));
        events.add(new Event("Event 14", "Location 14", "Date 14", "Time 14", "Description 14", R.drawable.event_image_dummy));
        events.add(new Event("Event 15", "Location 15", "Date 15", "Time 15", "Description 15", R.drawable.event_image_dummy));
        return events;
    }
}
