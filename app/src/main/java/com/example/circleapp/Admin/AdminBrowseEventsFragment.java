package com.example.circleapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.EventDisplay.EventAdapter;
import com.example.circleapp.EventDisplay.EventDetailsActivity;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;

import java.util.ArrayList;

public class AdminBrowseEventsFragment extends Fragment {
    ListView listView;
    Button backButton;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    EventAdapter adapter;

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a ListView
     * in combination with an instance of the EventAdapter class to display all events retrieved from
     * the Firestore database. When admin clicks on an event item, it will launch the
     * EventDetailsActivity for that event.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return                   The View for the fragment's UI, or null
     * @see EventDetailsActivity
     * @see EventAdapter
     * @see FirebaseManager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_browse_events, container, false);
        listView = rootView.findViewById(R.id.list_view);
        backButton = rootView.findViewById(R.id.back_button);

        adapter = new EventAdapter(getContext(), new ArrayList<>()); // Initialize adapter
        listView.setAdapter(adapter);

        loadEvents(); // Load events from Firebase

        // ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event event = (Event) parent.getItemAtPosition(position);
            eventClicked(event);
        });

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        return rootView;
    }

    /**
     * Loads events from Firebase and updates the ListView.
     */
    private void loadEvents() {
        firebaseManager.getAllEvents(events -> {
            adapter.clear();
            adapter.addAll(events);
        });
    }

    /**
     * Handles clicks on event items.
     *
     * @param event The clicked event
     */
    private void eventClicked(Event event) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("source", "AdminBrowseEventsFragment");
        intent.putExtra("event", event);
        startActivity(intent);
    }
}