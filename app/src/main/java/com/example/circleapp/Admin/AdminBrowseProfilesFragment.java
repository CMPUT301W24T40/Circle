package com.example.circleapp.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.example.circleapp.UserDisplay.AttendeeAdapter;
import com.example.circleapp.UserDisplay.UserDetailsActivity;

import java.util.ArrayList;

public class AdminBrowseProfilesFragment extends Fragment {
    ListView listView;
    Button backButton;
    Button notifyButton;
    Button mapButton;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    AttendeeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guestlist_admin, container, false);
        listView = rootView.findViewById(R.id.list_view);
        backButton = rootView.findViewById(R.id.back_button);
        notifyButton = rootView.findViewById(R.id.notify_button);
        mapButton = rootView.findViewById(R.id.map_button);
        notifyButton.setVisibility(View.INVISIBLE);
        mapButton.setVisibility(View.INVISIBLE);

        adapter = new AttendeeAdapter(rootView.getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        loadUsers(); // Load users from Firebase

        // ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Attendee attendee = (Attendee) parent.getItemAtPosition(position);
            attendeeClicked(attendee);
        });

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        return rootView;
    }

    /**
     * Loads attendees from Firebase and updates the ListView.
     */
    private void loadUsers() {
        firebaseManager.getAllUsers(users -> {
            adapter.clear();
            adapter.addAll(users);
        });
    }

    /**
     * Handles clicks on attendee items.
     *
     * @param attendee The clicked attendee
     */
    private void attendeeClicked(Attendee attendee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Select one of the following options:");
        builder.setPositiveButton("View user details", (dialog, which) -> {
            Intent intent = new Intent(getContext(), UserDetailsActivity.class);
            intent.putExtra("attendee", attendee);
            startActivity(intent);
        });
        builder.setNegativeButton("Delete user", (dialog, which) -> firebaseManager.deleteUser(attendee.getID()));
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
