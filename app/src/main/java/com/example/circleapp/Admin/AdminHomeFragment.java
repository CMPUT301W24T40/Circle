package com.example.circleapp.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.circleapp.R;

/**
 * This class is used to display the homepage of Admin interface (based on data in Firestore).
 */
public class AdminHomeFragment extends Fragment {
    Button browseUsers;
    Button browseEvents;
    Button browseImages;

    /**
     * Called to have the fragment instantiate its user interface view. The fragment simply displays
     * three buttons. When the admin clicks on any button, it will take the admin to the corresponding
     * fragment (AdminBrowseUsersFragment, AdminBrowseEventsFragment, AdminBrowseImagesFragment).
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return                   The View for the fragment's UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        browseUsers = view.findViewById(R.id.browse_users_button);
        browseEvents = view.findViewById(R.id.browse_events_button);
        browseImages = view.findViewById(R.id.browse_images_button);

        browseUsers.setOnClickListener(v -> replaceFragment(new AdminBrowseUsersFragment()));

        browseEvents.setOnClickListener(v -> replaceFragment(new AdminBrowseEventsFragment()));

        browseImages.setOnClickListener(v -> replaceFragment(new AdminBrowseImagesFragment()));

        return view;
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to replace with
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}