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
import com.example.circleapp.QRCode.ScanQRActivity;
import com.example.circleapp.R;

import java.util.ArrayList;

/**
 * This class is used to display a user's registered events.
 */
public class YourEventsFragment extends Fragment {
    ListView listView;
    Button scanButton;
    EventAdapter adapter;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a ListView
     * in combination with an instance of the EventAdapter class to display a user's registered
     * events retrieved from the Firestore database. When user clicks on an event item, it will launch the
     * EventDetailsActivity for that event.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return                   Return the View for the fragment's UI, or null
     * @see EventDetailsActivity
     * @see EventAdapter
     * @see FirebaseManager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_events, container, false);

        // Initialize views
        listView = rootView.findViewById(R.id.list_view);
        scanButton = rootView.findViewById(R.id.scan_button);

        // Scan button click listener
        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), ScanQRActivity.class);
            startActivity(intent);
        });

        // Initialize adapter and set it to ListView
        adapter = new EventAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        // Load registered events
        loadRegisteredEvents();

        // ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event event = (Event) parent.getItemAtPosition(position);
            eventClicked(event);
        });

        return rootView;
    }

    /**
     * Load registered events.
     */
    private void loadRegisteredEvents() {
        firebaseManager.getRegisteredEvents(events -> {
            adapter.clear();
            adapter.addAll(events);
        });
    }

    /**
     * Handle item clicks.
     *
     * @param event The clicked event
     */
    private void eventClicked(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("source", "YourEventsFragment");
        intent.putExtra("event", event);
        startActivity(intent);
    }
}