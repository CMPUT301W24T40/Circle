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

public class AdminHomeFragment extends Fragment {
    Button browseProfiles;
    Button browseEvents;
    Button browseImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        browseProfiles = view.findViewById(R.id.browse_profiles_button);
        browseEvents = view.findViewById(R.id.browse_events_button);
        browseImages = view.findViewById(R.id.browse_images_button);

        browseProfiles.setOnClickListener(v -> replaceFragment(new AdminBrowseProfilesFragment()));

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