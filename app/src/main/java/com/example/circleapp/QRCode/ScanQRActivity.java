package com.example.circleapp.QRCode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.circleapp.EventDisplay.BrowseEventDetailsActivity;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.MainActivity;
import com.example.circleapp.Profile.ProfileFragment;
import com.example.circleapp.R;
import com.example.circleapp.TempUserInfoActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is a class used for scanning QR codes.
 */
public class ScanQRActivity extends AppCompatActivity {
    private static final String CHECK_IN = "check-in";
    private static final String DETAILS = "details";
    private static final String ADMIN = "admin";
    private static final String LOCATION_PERMISSION = "LocationPermission";
    private static final String LOCATION_PERMISSION_GRANTED = "location_permission_granted";

    private boolean locationPermissionGranted;
    private FirebaseManager manager;

    /**
     * When the Activity is created, the ability to scan QR codes is initiated,
     * opening up a camera for scanning QR codes.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_your_events);

        locationPermissionGranted = getSharedPreferences(LOCATION_PERMISSION, Context.MODE_PRIVATE)
                .getBoolean(LOCATION_PERMISSION_GRANTED, false);

        initiateScan();
        setupOnBackPressedCallback();
    }

    /**
     * This starts up the camera for scanning a QR code.
     */
    private void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Here!");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    /**
     * Sets up the onBackPressed callback to finish the activity when back button is pressed.
     */
    private void setupOnBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    /**
     * This is used to handle the result of the QR code scan. If the QR code is a check-in code,
     * the user is checked into the event. If the QR code is a details code, the user is taken to
     * the event details page.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        manager = FirebaseManager.getInstance();

        if (result == null || result.getContents() == null) {
            showToastAndFinish("Try Again");
            return;
        }

        handleScanResult(result);
    }

    /**
     * Handles the result of the QR code scan based on the type of QR code.
     *
     * @param result The result of the QR code scan.
     */
    private void handleScanResult(IntentResult result) {
        String[] parts = result.getContents().split("~");
        String qrType = parts[0];

        if (CHECK_IN.equals(qrType)) {
            String eventID = parts.length > 1 ? parts[1] : "";
            handleCheckInScan(eventID);
        }
        else if (DETAILS.equals(qrType)) {
            String eventID = parts.length > 1 ? parts[1] : "";
            handleDetailsScan(eventID);
        }
        else if (ADMIN.equals(qrType)) { handleAdminScan(); }
        else { handleCheckInIDScan(result.getContents()); }
    }

    /**
     * Handles scanning result of a check-in code.
     *
     * @param eventID The ID of the event.
     */
    private void handleCheckInScan(String eventID) {
        manager.getEvent(eventID, event -> {
            if (event == null) {
                showToastAndFinish("No event found with this check-in ID");
                return;
            }

            manager.checkUserExists(exists -> {
                if (!exists) {
                    startTempUserInfoActivity();
                    return;
                }

                if (locationPermissionGranted) { getCurrentLocation(eventID); }
                else { checkInEventWithUnavailableLocation(eventID); }

                showToastAndFinish("Checking in to event: " + event.getEventName());
            });
        });
    }

    /**
     * Handles scanning result of a event details code.
     *
     * @param eventID The ID of the event.
     */
    private void handleDetailsScan(String eventID) {
        manager.getEvent(eventID, event -> {
            Intent intent = new Intent(this, BrowseEventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Handles scanning result of a check-in ID.
     *
     * @param checkInID The check-in ID.
     */
    private void handleCheckInIDScan(String checkInID) {
        manager.getEventByCheckInID(checkInID, event -> {
            if (event == null) {
                showToastAndFinish("No event found with this check-in ID");
                return;
            }

            manager.checkUserExists(exists -> {
                if (!exists) {
                    showToastAndFinish("User does not exist");
                    return;
                }

                if (locationPermissionGranted) { getCurrentLocation(event.getID()); }
                else { checkInEventWithUnavailableLocation(event.getID()); }

                showToastAndFinish("Checking in to event: " + event.getEventName());
            });
        });
    }

    /**
     * Handles scanning result of an admin code.
     */
    private void handleAdminScan() {
        runOnUiThread(() -> {
            View pwordView = getLayoutInflater().inflate(R.layout.admin_password_entry, null);
            EditText userInput = pwordView.findViewById(R.id.password_entry);

            AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRActivity.this);
            builder.setView(pwordView).setTitle("Ascension");
            builder.setMessage("To become an admin, enter the secret password:");
            builder.setPositiveButton("Enter", (dialog, which) -> {
                String password = userInput.getText().toString();
                manager.becomeAdmin(password);

                manager.isAdmin(exists -> {
                    if (exists) {
                        ProfileFragment.isAdmin = true;
                        showToastAndFinish("Go to the profile tab to check out your new powers!");
                        Intent intent = new Intent(ScanQRActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else { showToastAndFinish("Failed to become admin"); }
                });
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    /**
     * Starts TempUserInfoActivity.
     */
    private void startTempUserInfoActivity() {
        Intent intent = new Intent(ScanQRActivity.this, TempUserInfoActivity.class);
        startActivity(intent);
        showToastAndFinish("User does not exist");
    }

    /**
     * Retrieves the current location.
     *
     * @param eventID The ID of the event.
     */
    private void getCurrentLocation(String eventID) {
        FusedLocationProviderClient fusedLocationProviderClient;
        if (ActivityCompat.checkSelfPermission(ScanQRActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ScanQRActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ScanQRActivity.this);
        }
        else {
            Log.d("location","poo");
            checkInEventWithUnavailableLocation(eventID);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            Log.d("location", String.valueOf(location));
            if (location != null) { manager.checkInEvent(eventID, location); }
            else { checkInEventWithUnavailableLocation(eventID); }
        });
    }

    /**
     * Checks in the event when location is unavailable.
     *
     * @param eventID The ID of the event.
     */
    private void checkInEventWithUnavailableLocation(String eventID) {
        manager.checkInEvent(eventID, null);
    }

    /**
     * Displays a toast message and finishes the activity.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToastAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}