package com.example.circleapp.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;

/**
 * This class is used to display the profile of a user.
 */
public class ProfileFragment extends Fragment {
    public static boolean ProfileMade;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseManager.checkProfileExists(firebaseManager.getPhoneID(), exists -> {
            if (exists) {
                navigateToUserProfileFragment();
            } else {
                if (isAdded() && getActivity() != null) {
                    navigateToStartupProfileFragment();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ProfileMade) {
            navigateToUserProfileFragment();
        } else {
            navigateToStartupProfileFragment();
        }
    }

    private void navigateToUserProfileFragment() {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, userProfileFragment)
                .addToBackStack(null) // Add to back stack
                .commit();
    }

    private void navigateToStartupProfileFragment() {
        StartupProfileFragment startupProfileFragment = new StartupProfileFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, startupProfileFragment)
                .addToBackStack(null) // Add to back stack
                .commit();
    }
}