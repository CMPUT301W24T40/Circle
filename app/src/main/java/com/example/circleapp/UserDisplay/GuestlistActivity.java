package com.example.circleapp.UserDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.example.circleapp.SendNotificationActivity;

import java.util.ArrayList;

/**
 * This class is used to display all users registered to the given event.
 */
public class GuestlistActivity extends AppCompatActivity {
    Button backButton;
    ListView listView;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    AttendeeAdapter adapter;
    Event event;
    Button mapButton;
    Button notficationButton;
    ArrayList<Attendee> attendees;

    /**
     * The activity uses a ListView in combination with an instance of the AttendeeAdapter class to
     * users that are signed up for the given event (retrieved from Firebase). When user clicks on
     * an attendee item, it will launch the UserDetailsActivity for that attendee.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @see UserDetailsActivity
     * @see AttendeeAdapter
     * @see FirebaseManager
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestlist);
        listView = findViewById(R.id.list_view);

        adapter = new AttendeeAdapter(this, new ArrayList<>()); // Initialize adapter
        listView.setAdapter(adapter);

        event = getIntent().getParcelableExtra("event");
        loadRegisteredUsers(event.getID()); // Load users from Firebase

        // ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Attendee attendee = (Attendee) parent.getItemAtPosition(position);
            attendeeClicked(attendee);
        });

        // Back button click listener
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        attendees = firebaseManager.getRegisteredUserTokens(event.getID());

        // for notifications
        notficationButton = findViewById(R.id.notify_button);
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

        mapButton = findViewById(R.id.map_button);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapViewActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads attendees from Firebase and updates the ListView.
     */
    private void loadRegisteredUsers(String eventID) {
        firebaseManager.getRegisteredUsers(users -> {
            adapter.clear();
            adapter.addAll(users);
        }, eventID);
    }

    /**
     * Handles clicks on attendee items.
     *
     * @param attendee The clicked attendee
     */
    private void attendeeClicked(Attendee attendee) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra("source", "GuestlistActivity");
        intent.putExtra("attendee", attendee);
        startActivity(intent);
    }
}