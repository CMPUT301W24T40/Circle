package com.example.circleapp.EventDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;

import java.util.ArrayList;

/**
 * This class is used to display a user's created events.
 */
public class CreatedEventsFragment extends Fragment {
    ListView listView;
    EventAdapter adapter;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a ListView
     * in combination with an instance of the EventAdapter class to display the events a user has
     * created, retrieved from the Firestore database. When user clicks on an event item, it
     * will launch the EventDetailsActivity for that event.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in
     *                           the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to.
     *                           The fragment should not add the view itself, but this can be used to
     *                           generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return                   Return the View for the fragment's UI, or null
     * @see CreatedEventDetailsActivity
     * @see EventAdapter
     * @see FirebaseManager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_created_events, container, false);

        listView = rootView.findViewById(R.id.list_view);

        adapter = new EventAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        loadCreatedEvents();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event event = (Event) parent.getItemAtPosition(position);
            eventClicked(event);
        });

        return rootView;
    }

    /**
     * Load registered events.
     */
    private void loadCreatedEvents() {
        firebaseManager.getCreatedEvents(events -> {
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
        Intent intent = new Intent(getContext(), CreatedEventDetailsActivity.class);
        intent.putExtra("source", "CreatedEventsFragment");
        intent.putExtra("event", event);
        startActivity(intent);
    }
}