package com.example.circleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
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
                finish();
            }
        });

    }
}