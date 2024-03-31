package com.example.circleapp.QRCode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.circleapp.EventDisplay.EventDetailsActivity;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is a class used for scanning QR codes.
 */
public class ScanQRActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    Location currentLocation;
    FirebaseManager manager;

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

        SharedPreferences sharedPreferences = getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);
        locationPermissionGranted = sharedPreferences.getBoolean("location_permission_granted", false);

        initiateScan();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        manager = FirebaseManager.getInstance();
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
                finish();
            } else {
                String[] parts = result.getContents().split("~");
                if (parts.length == 2) {
                    String qrType = parts[0];
                    String eventID = parts[1];
                    if (qrType.equals("check-in")) {
                        manager.getEvent(eventID, event -> {
                            if (event != null) {
                                manager.checkUserExists(exists -> {
                                    if (exists) {
                                        if (locationPermissionGranted) {
                                            getCurrentLocation(new LocationCallback() {
                                                @Override
                                                public void onLocationResult(Location location) {
                                                    currentLocation = location;
                                                    handleLocationSuccess(eventID);
                                                }

                                                @Override
                                                public void onLocationUnavailable() {
                                                    Toast.makeText(ScanQRActivity.this, "Location is unavailable", Toast.LENGTH_SHORT).show();
                                                    handleLocationUnavailable(eventID);
                                                }
                                            });
                                        } else {
                                            handleLocationUnavailable(eventID);
                                        }
                                        Toast.makeText(this, "Checking in to event: " + event.getEventName(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show();
                                    }
                                    finish();
                                });
                            } else {
                                Toast.makeText(this, "No event found with this check-in ID", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    } else if (qrType.equals("details")) {
                        manager.getEvent(eventID, event -> {
                            Intent intent = new Intent(this, EventDetailsActivity.class);
                            intent.putExtra("event", event);
                            startActivity(intent);
                            finish();
                        });
                    }
                } else {
                    String checkInID = result.getContents();
                    manager.getEventByCheckInID(checkInID, event -> {
                        if (event != null) {
                            manager.checkUserExists(exists -> {
                                if (exists) {
                                    if (locationPermissionGranted) {
                                        getCurrentLocation(new LocationCallback() {
                                            @Override
                                            public void onLocationResult(Location location) {
                                                currentLocation = location;
                                                handleLocationSuccess(event.getID());
                                            }

                                            @Override
                                            public void onLocationUnavailable() {
                                                Toast.makeText(ScanQRActivity.this, "Location is unavailable", Toast.LENGTH_SHORT).show();
                                                handleLocationUnavailable(event.getID());
                                            }
                                        });
                                    } else {
                                        handleLocationUnavailable(event.getID());
                                    }
                                    Toast.makeText(this, "Checking in to event: " + event.getEventName(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show();
                                }
                                finish();
                            });
                        } else {
                            Toast.makeText(this, "No event found with this check-in ID", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Callback interface for operations with a user's location.
     */
    public interface LocationCallback {
        /**
         * Called when a location result is available.
         * @param location The retrieved location.
         */
        void onLocationResult(Location location);

        /**
         * Called when the location is unavailable.
         */
        void onLocationUnavailable();
    }

    /**
     * Handles successful location retrieval by checking in the user to the specified event.
     *
     * @param eventID The ID of the event to check in to.
     */
    private void handleLocationSuccess(String eventID) {
        manager.checkInEvent(eventID, currentLocation);
    }

    /**
     * Handles situation where location is unavailable by attempting to check in the user to the
     * specified event with null location.
     *
     * @param eventID The ID of the event to check in to.
     */
    private void handleLocationUnavailable(String eventID) {
        manager.checkInEvent(eventID, null);
    }

    /**
     * Retrieves the current location asynchronously.
     * @param callback The callback to be invoked when the location is retrieved or unavailable.
     */
    private void getCurrentLocation(LocationCallback callback) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ScanQRActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationUnavailable();
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocationResult(location);
                    } else {
                        callback.onLocationUnavailable();
                    }
                });
    }
}