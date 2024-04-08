package com.example.circleapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.circleapp.EventDisplay.BrowseEventsFragment;
import com.example.circleapp.EventDisplay.YourEventsFragment;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.Profile.ProfileFragment;
import com.example.circleapp.databinding.ActivityMainBinding;

/**
 * The main activity of the application.
 */
public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {}
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("You will not receive any notifications from this app." +
                            "You can change this by going to the app permissions in Settings.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            });

    ActivityMainBinding binding;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in savedInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        askNotificationPermission();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager.setPhoneID(this);

        replaceFragment(new BrowseEventsFragment());

        binding.bottomNavigationView.setVisibility(View.VISIBLE);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
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

    /**
     * This method is used to ask the user if they want to be sent notifications from the app.
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // empty for now can be filled later, just education UI about what having notifs on means
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * Sets the visibility of the navigation bar.
     *
     * @param isVisible {@code true} to make the navigation bar visible, {@code false} to hide it.
     */
    public void setNavBarVisibility(boolean isVisible) {
        if (isVisible) { binding.bottomNavigationView.setVisibility(View.VISIBLE); }
        else { binding.bottomNavigationView.setVisibility(View.GONE); }
    }
}