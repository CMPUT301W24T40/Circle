package com.example.circleapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.circleapp.EventDisplay.BrowseEventsFragment;
import com.example.circleapp.EventDisplay.YourEventsFragment;
import com.example.circleapp.Profile.ProfileFragment;
import com.example.circleapp.databinding.ActivityMainBinding;

/**
 * The main activity of the application.
 */
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding; // View binding instance

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new BrowseEventsFragment()); // Start with BrowseEventsFragment

        // Bottom navigation item selection listener
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Replace fragment based on the selected item
            if (item.getItemId() == R.id.your_events_item) {
                replaceFragment(new YourEventsFragment());
                return true;
            }

            if (item.getItemId() == R.id.browse_events_item) {
                replaceFragment(new BrowseEventsFragment());
                return true;
            }

            if (item.getItemId() == R.id.profile_item) {
                replaceFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to replace with
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}