package com.example.circleapp.UserDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.EventDisplay.CreatedEventDetailsActivity;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.example.circleapp.SendNotificationActivity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A fragment to display the list of checked-in users for an event.
 */
public class CheckedInUsersFragment extends Fragment {
    ListView listView;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    AttendeeAdapter adapter;
    Event event;
    Button mapButton;
    Button notficationButton;
    ArrayList<Attendee> attendees;

    /**
     * Called to have the fragment instantiate its user interface view. Uses a ListView combined with
     * an AttendeeAdapter to display the users who have checked in for a given event.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return                   Return the View for the fragment's UI, or null.
     * @see CreatedEventDetailsActivity
     * @see AttendeeAdapter
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checked_in_users, container, false);
        listView = view.findViewById(R.id.list_view);

        adapter = new AttendeeAdapter(getContext(), new ArrayList<>(), true);
        listView.setAdapter(adapter);

        event = requireArguments().getParcelable("event");
        loadCheckedInUsers(Objects.requireNonNull(event).getID());

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Attendee attendee = (Attendee) parent.getItemAtPosition(position);
            attendeeClicked(attendee);
        });

        attendees = firebaseManager.getRegisteredUserTokens(event.getID());

        notficationButton = view.findViewById(R.id.notify_button);
        notficationButton.setOnClickListener(v -> {
            ArrayList<String> tokens = new ArrayList<>();
            for (Attendee attendee : attendees) {
                assert attendee != null;
                String token = attendee.getToken();
                tokens.add(token);
            }
            Intent intent = new Intent(v.getContext(), SendNotificationActivity.class);
            intent.putStringArrayListExtra("tokens", tokens);
            intent.putExtra("event name", event.getEventName());
            startActivity(intent);
        });

        mapButton = view.findViewById(R.id.map_button);

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MapViewActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Load checked-in users for the given event.
     * @param eventID The ID of the event.
     */
    private void loadCheckedInUsers(String eventID) {
        firebaseManager.getCheckedInAttendees(eventID, users -> {
            for (Attendee user : users) {
                firebaseManager.getCheckInCount(eventID, user.getID(), count -> {
                    user.setCheckInCount(count);
                    adapter.notifyDataSetChanged();
                });
            }
            adapter.clear();
            adapter.addAll(users);
        });
    }

    /**
     * Handle click on an attendee item.
     * @param attendee The clicked attendee.
     */
    private void attendeeClicked(Attendee attendee) {
        Intent intent = new Intent(getContext(), UserDetailsActivity.class);
        intent.putExtra("source", "GuestlistActivity");
        intent.putExtra("attendee", attendee);
        startActivity(intent);
    }
}