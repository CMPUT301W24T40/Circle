package com.example.circleapp.EventDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.QRCode.ScanQRActivity;
import com.example.circleapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * This class is used to display a user's registered events.
 */
public class YourEventsFragment extends Fragment {

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a TabLayout
     * to display "two fragments at once". When user clicks on this page from navigation bar, if there
     * is no user currently "logged in", fragment will simply display a statement to make a profile.
     * Otherwise, this page will have a tabbed setup, where the clicking of the two tabs launches either
     * the AttendingEventsFragment or CreatedEventsFragment. As the names suggest, the two tabs allow
     * the user to view either the events that they have signed up for or the events that they have
     * created themselves.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return                   The View for the fragment's UI, or null
     * @see CreatedEventsFragment
     * @see RegisteredEventsFragment
     * @see FirebaseManager
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_your_events, container, false);

        ViewPager2 viewPager = rootView.findViewById(R.id.view_pager);
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
        Button scanButton = rootView.findViewById(R.id.scan_button);

        viewPager.setAdapter(new EventsPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Attending");
                            break;
                        case 1:
                            tab.setText("Organizing");
                            break;
                    }
                }).attach();

        // Scan button click listener
        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(rootView.getContext(), ScanQRActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private static class EventsPagerAdapter extends FragmentStateAdapter {

        EventsPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new RegisteredEventsFragment();
                case 1:
                    return new CreatedEventsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}