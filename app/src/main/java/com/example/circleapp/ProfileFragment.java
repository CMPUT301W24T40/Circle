package com.example.circleapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    CheckBox geolocationEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        firstNameEditText = (EditText) getView().findViewById(R.id.first_name);
//        lastNameEditText = (EditText) getView().findViewById(R.id.last_name);
//        emailEditText = (EditText) getView().findViewById(R.id.email);
//        phoneNumberEditText = (EditText) getView().findViewById(R.id.phone_number);
//        geolocationEditText = (CheckBox) getView().findViewById(R.id.geolocation);
////
//        String firstName = firstNameEditText.getText().toString();
//        String lastName = lastNameEditText.getText().toString();
//        String name = firstName + lastName;
//        String email = emailEditText.getText().toString();
//        Integer phoneNumber = Integer.valueOf(phoneNumberEditText.getText().toString());
//        boolean isGeoEnabled = geolocationEditText.isEnabled();
//
//        Attendee user = new Attendee(name, email);
//        user.setGeoEnabled(isGeoEnabled);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
