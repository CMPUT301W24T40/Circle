package com.example.circleapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ProfileFragment extends Fragment {

    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phoneNumber;
    CheckBox geolocation;
    Button scanButton;
    Button makeProfile;
    Button editProfile;
    ImageView profilePic;
    RelativeLayout makeProfileLayout;
    RelativeLayout userProfileLayout;

    private ActivityResultLauncher<Intent> launcher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firstName = view.findViewById(R.id.first_name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);
        geolocation = view.findViewById(R.id.edit_geolocation);
        makeProfile = view.findViewById(R.id.add_profile_details);
        editProfile = view.findViewById(R.id.edit_profile_button);
        profilePic = view.findViewById(R.id.edit_pfp);

        /*
        this is what receives the new Attendee (made by user inputs) from the MakeProfileActivity.
        If it got results (always will in this case because the confirm button from MakeProfileActivity
        will always have the result code as RESULT_OK), it will change the ProfileLayout from that
        startup "Make a Profile" layout to their actual profile information!
        Problem for now -> it does NOT stay the same after you switch tabs on the bottom nav bar
        */
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // checks to see if it got any results from MakeProfileFragment
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        // Extracts our Attendee object from the Bundle
                        Attendee ourUser = bundle.getParcelable("user");

                        // Switches the layouts, I just turn the startup layout invisible
                        makeProfileLayout = view.findViewById(R.id.startup_profile_layout);
                        makeProfileLayout.setVisibility(View.INVISIBLE);
                        userProfileLayout = view.findViewById(R.id.user_profile_layout);
                        userProfileLayout.setVisibility(View.VISIBLE);
                        // sets the text for the Profile layout to the attributes of
                        // the attendee
                        firstName.setText(ourUser.getName());
                        email.setText(ourUser.getEmail());

                        // makes user only able to edit the geo in edit mode
                        String checked = "Geolocation: ENABLED";
                        String unchecked = "Geolocation: DISABLED";
                        geolocation.setClickable(false);
                        if (ourUser.isGeoEnabled()) {
                            geolocation.setText(checked);
                            geolocation.setChecked(true);
                        }
                        else {
                            geolocation.setText(unchecked);
                            geolocation.setChecked(false);
                        }

                        // puts profile pic onto layout
                        Glide.with(ProfileFragment.this).load(ourUser.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(profilePic);;
                    }
                }
        );
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
                launcher.launch(intent);
            }
        });

        makeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
                launcher.launch(intent);
            }
        });

        return view;
    }
}
