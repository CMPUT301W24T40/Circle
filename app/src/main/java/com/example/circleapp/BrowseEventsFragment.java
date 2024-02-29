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
        for (int i = 1; i <= 15; i++) {
            events.add(new Event("Event " + i, "Location " + i, "Date " + i, "Time " + i, "Description " + i));
        }
        return events;
    }
}