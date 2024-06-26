package com.example.circleapp.EventDisplay;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.example.circleapp.TempUserInfoActivity;

import java.util.ArrayList;

/**
 * This class is used to display all existing events (based on data in Firestore).
 */
public class BrowseEventsFragment extends Fragment {
    ListView listView;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Button addEvent;
    EventAdapter adapter;

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a ListView
     * in combination with an instance of the EventAdapter class to display all events retrieved from
     * the Firestore database. When user clicks on an event item, it will launch the
     * EventDetailsActivity for that event. The user can also click on the "Create Event" button to
     * launch the CreateEventActivity. The UI/adapter will then be updated with the new event.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return                   The View for the fragment's UI, or null
     * @see BrowseEventDetailsActivity
     * @see CreateEventActivity
     * @see EventAdapter
     * @see FirebaseManager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_events, container, false);
        listView = rootView.findViewById(R.id.list_view);
        addEvent = rootView.findViewById(R.id.add_event_button);

        addEvent.setOnClickListener(v -> firebaseManager.checkUserExists(exists -> {
            if (exists) {
                Intent intent = new Intent(rootView.getContext(), CreateEventActivity.class);
                startActivity(intent);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setTitle("Details needed");
                builder.setMessage("Before creating an event, we need some details from you");
                builder.setPositiveButton("Let's go!", (dialog, which) -> {
                    Intent intent = new Intent(rootView.getContext(), TempUserInfoActivity.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }));

        adapter = new EventAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        loadEvents();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event event = (Event) parent.getItemAtPosition(position);
            eventClicked(event);
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
        Intent intent = new Intent(getContext(), BrowseEventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }
}