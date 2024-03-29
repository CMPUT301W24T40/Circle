package com.example.circleapp.QRCode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.circleapp.EventDisplay.EventDetailsActivity;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is a class used for scanning QR codes.
 */
public class ScanQRActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    final private int FINE_PERMISSION_CODE = 100;
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

        initiateScan();

        // This is used to handle the back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    /**
     * This starts up the camera for scanning a QR code
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
                                        getCurrentLocation(new LocationCallback() {
                                            @Override
                                            public void onLocationResult(Location location) {
                                                currentLocation = location;
                                                handleLocationSuccess(eventID);
                                            }

                                            @Override
                                            public void onLocationUnavailable() {
                                                Toast.makeText(ScanQRActivity.this, "Location is unavailable", Toast.LENGTH_SHORT).show();
                                            }
                                        });

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
                    // If the QR code doesn't have a prefix, assume it's a checkInID
                    String checkInID = result.getContents();
                    manager.getEventByCheckInID(checkInID, event -> {
                        if (event != null) {
                            manager.checkUserExists(exists -> {
                                if (exists) {
                                    getCurrentLocation(new LocationCallback() {
                                        @Override
                                        public void onLocationResult(Location location) {
                                            currentLocation = location;
                                            handleLocationSuccess(event.getID());
                                        }

                                        @Override
                                        public void onLocationUnavailable() {
                                            Toast.makeText(ScanQRActivity.this, "Location is unavailable", Toast.LENGTH_SHORT).show();
                                        }
                                    });

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


    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationUnavailable();
    }

    private void handleLocationSuccess(String eventID) {
        manager.checkInEvent(eventID, currentLocation);
    }

    private void getCurrentLocation(LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(ScanQRActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ScanQRActivity.this);

            fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        callback.onLocationResult(location);;
                                    } else {
                                        callback.onLocationUnavailable();
                                    }
                                }
                            });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(new LocationCallback() {
                    @Override
                    public void onLocationResult(Location location) {
                        currentLocation = location;
                    }

                    @Override
                    public void onLocationUnavailable() {
                        Toast.makeText(ScanQRActivity.this, "Location is unavailable", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Location Disabled", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}