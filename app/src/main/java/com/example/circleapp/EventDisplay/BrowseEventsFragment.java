package com.example.circleapp.EventDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;

import java.util.ArrayList;

public class BrowseEventsFragment extends Fragment {
    ListView listView;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Button addEvent;
    EventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_events, container, false);
        listView = rootView.findViewById(R.id.list_view);
        addEvent = rootView.findViewById(R.id.add_event_button);

        addEvent.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), CreateEventActivity.class);
            startActivity(intent);
        });

        adapter = new EventAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        loadEvents();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event event = (Event) parent.getItemAtPosition(position);
            eventClicked(event);
        });

        return rootView;
    }

    private void loadEvents() {
        firebaseManager.getAllEvents(events -> {
            adapter.clear();
            adapter.addAll(events);
        });
    }

    // handle item clicks
    private void eventClicked(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("source", "BrowseEventsFragment");
        intent.putExtra("event", event);
        startActivity(intent);
    }
}