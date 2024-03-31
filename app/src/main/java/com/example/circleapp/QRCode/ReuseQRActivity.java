package com.example.circleapp.QRCode;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is a class used for reusing existing QR codes for Check-in.
 */
public class ReuseQRActivity extends AppCompatActivity {
    private FirebaseManager firebaseManager;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        event = getIntent().getParcelableExtra("event");
        firebaseManager = new FirebaseManager();
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
     * Handles the result of an activity launched for a result.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The data returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                String[] parts = result.getContents().split("~");
                if (parts.length == 2 && (parts[0].equals("check-in") || parts[0].equals("details"))) {
                    String eventID = parts[1];
                    firebaseManager.getEvent(eventID, event -> {
                        if (event != null) {
                            new AlertDialog.Builder(this)
                                    .setTitle("QR Code In Use")
                                    .setMessage("This QR code is already in use.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .show();
                        }
                        else {
                            new AlertDialog.Builder(this)
                                    .setTitle("Corrupted QR Code")
                                    .setMessage("This QR code is corrupted.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .show();
                        }
                    });
                }
                else {
                    String checkInID = result.getContents();
                    event.setCheckInID(checkInID);
                    firebaseManager.editEvent(event);
                    Toast.makeText(this, "This QR code can now be used to check into "+ event.getEventName(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}