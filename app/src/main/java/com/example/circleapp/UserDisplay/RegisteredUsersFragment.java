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
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.example.circleapp.SendNotificationActivity;

import java.util.ArrayList;

public class RegisteredUsersFragment extends Fragment {
    Button backButton;
    ListView listView;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    AttendeeAdapter adapter;
    Event event;
    Button mapButton;
    Button notficationButton;
    ArrayList<Attendee> attendees;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_users, container, false);
        listView = view.findViewById(R.id.list_view);

        adapter = new AttendeeAdapter(getContext(), new ArrayList<>(), false); // Initialize adapter
        listView.setAdapter(adapter);

        event = getArguments().getParcelable("event");
        loadRegisteredUsers(event.getID()); // Load users from Firebase

        // ListView item click listener
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Attendee attendee = (Attendee) parent.getItemAtPosition(position);
            attendeeClicked(attendee);
        });


        attendees = firebaseManager.getRegisteredUserTokens(event.getID());

        // for notifications
        notficationButton = view.findViewById(R.id.notify_button);
        /*
          Sends user to new activity to write out a notification to send to attendees of event
         */
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

    private void loadRegisteredUsers(String eventID) {
        firebaseManager.getRegisteredUsers(users -> {
            adapter.clear();
            adapter.addAll(users);
        }, eventID);
    }

    private void attendeeClicked(Attendee attendee) {
        Intent intent = new Intent(getContext(), UserDetailsActivity.class);
        intent.putExtra("source", "GuestlistActivity");
        intent.putExtra("attendee", attendee);
        startActivity(intent);
    }
}