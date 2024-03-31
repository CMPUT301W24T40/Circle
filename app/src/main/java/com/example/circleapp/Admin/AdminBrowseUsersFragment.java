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

/**
 * This class is used to display all existing users in the admin interface (based on data in Firestore).
 */
public class AdminBrowseUsersFragment extends Fragment {
    ListView listView;
    Button backButton;
    Button notifyButton;
    Button mapButton;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    AttendeeAdapter adapter;

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a ListView
     * in combination with an instance of the AttendeeAdapter class to display all users retrieved from
     * the Firestore database. When admin clicks on a user item, it will prompt the admin to either
     * delete that user or view their details.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return                   The View for the fragment's UI, or null
     * @see UserDetailsActivity
     * @see AttendeeAdapter
     * @see FirebaseManager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guestlist_admin, container, false);
        listView = rootView.findViewById(R.id.list_view);
        backButton = rootView.findViewById(R.id.back_button);
        notifyButton = rootView.findViewById(R.id.notify_button);
        mapButton = rootView.findViewById(R.id.map_button);
        notifyButton.setVisibility(View.INVISIBLE);
        mapButton.setVisibility(View.INVISIBLE);

        adapter = new AttendeeAdapter(rootView.getContext(), new ArrayList<>(), false);
        listView.setAdapter(adapter);

        loadUsers();

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
