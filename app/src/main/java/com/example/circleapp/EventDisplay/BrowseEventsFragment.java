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
    RecyclerView recyclerView;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Button addEvent;
    ArrayList<Event> events;
    EventAdapter adapter;
    EventAdapter.OnItemClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_events, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addEvent = rootView.findViewById(R.id.add_event_button);

        addEvent.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), CreateEventActivity.class);
            startActivity(intent);
        });

        // Set up the click listener
        listener = this::eventClicked;

        firebaseManager.getAllEvents(new FirebaseManager.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Event> events) {
                // Pass the click listener to the adapter
                adapter = new EventAdapter(events, listener);
                recyclerView.setAdapter(adapter);
            }
        });

        return rootView;
    }

    // handle item clicks
    private void eventClicked(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }
}