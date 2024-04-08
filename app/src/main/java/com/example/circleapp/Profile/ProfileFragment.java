package com.example.circleapp.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;

/**
 * This class is used to manage what page is displayed when the user
 * navigates to the Profile tab.
 */
public class ProfileFragment extends Fragment {
    public static boolean ProfileMade;
    public static boolean isAdmin;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    /**
     * View manages which fragment should be displayed when user clicks onto the Profile Tab. If
     * the user has a profile, the UserProfileFragment will be displayed; if not, the StartupProfileFragment
     * will be displayed. If the latter, the user can then choose to add details and create a profile,
     * which will launch the MakeProfileActivity.
     *
     * @param inflater          The LayoutInflater object that can be used to inflate
     *                          any views in the fragment,
     * @param container         If non-null, this is the parent view that the fragment's
     *                          UI should be attached to.  The fragment should not add the view itself,
     *                          but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return                  Returns the View shown to the user.
     * @see MakeProfileActivity
     * @see UserProfileFragment
     * @see StartupProfileFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseManager.checkProfileExists(firebaseManager.getPhoneID(), exists -> {
            if (exists) {
                ProfileMade = true;
                replaceFragment(new UserProfileFragment()); }
            else if (isAdded() && getActivity() != null) { replaceFragment(new StartupProfileFragment()); }
        });
    }

    /**
     * This method is called when the activity becomes visible to the user.
     * If a user profile is created, it replaces the current fragment with a user profile fragment.
     * Otherwise, it replaces it with a startup profile fragment.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (ProfileMade) { replaceFragment(new UserProfileFragment()); }
        else { replaceFragment(new StartupProfileFragment()); }
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to replace with
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}