package com.example.circleapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

// Activity that starts when User wants to make a profile
public class MakeProfileActivity extends AppCompatActivity {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    CheckBox geolocationEditText;
    Button confirmButton;
    // no images yet
    private final ProfileFragment fragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);


        firstNameEditText = findViewById(R.id.fname_edit);
        lastNameEditText = findViewById(R.id.lname_edit);
        emailEditText = findViewById(R.id.edit_email);
        phoneNumberEditText = findViewById(R.id.edit_number);
        geolocationEditText = findViewById(R.id.edit_geolocation);
        confirmButton = findViewById(R.id.confirm_edit_button);

        /*
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        Integer phoneNumber = Integer.valueOf(phoneNumberEditText.getText().toString());
        boolean isGeoEnabled = geolocationEditText.isEnabled();
        Attendee user = new Attendee(firstName, email);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        // user.setGeoEnabled(isGeoEnabled);
        */

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // after clicking confirm, gets all the user inputs from EditTexts etc.
                String firstName = firstNameEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // Creates a new user! But need to figure out how we'll be able to edit
                // an existing user because this makes an entirely new Attendee object each time
                Attendee user = new Attendee(firstName, email);

                // Stuffs the Attendee (user) object into a Bundle and then an Intent to be
                // sent back to the fragment
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                Intent intent = new Intent();
                intent.putExtras(bundle);

                // Letting the fragment know it has results or not
                // in this case it ALWAYS will, for now just to test
                setResult(Activity.RESULT_OK, intent);

                // Closes the activity
                finish();
            }
        });

    }
}