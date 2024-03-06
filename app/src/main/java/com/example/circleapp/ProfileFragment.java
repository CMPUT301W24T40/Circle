package com.example.circleapp;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
    Button makeProfile;
    Button editProfile;
    ImageView profilePic;
    RelativeLayout makeProfileLayout;
    RelativeLayout userProfileLayout;
    static boolean profileMade;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<Intent> launcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);
        geolocation = view.findViewById(R.id.edit_geolocation);
        makeProfile = view.findViewById(R.id.add_profile_details);
        editProfile = view.findViewById(R.id.edit_profile_button);
        profilePic = view.findViewById(R.id.edit_pfp);

        if (profileMade) {
            makeProfileLayout = view.findViewById(R.id.startup_profile_layout);
            makeProfileLayout.setVisibility(View.INVISIBLE);
            userProfileLayout = view.findViewById(R.id.user_profile_layout);
            userProfileLayout.setVisibility(View.VISIBLE);

            firstName.setText(sharedPreferences.getString("user_first_name", null));
            lastName.setText(sharedPreferences.getString("user_last_name", null));
            phoneNumber.setText(sharedPreferences.getString("user_phone_number", null));
            email.setText(sharedPreferences.getString("user_email", null));
        }

        else {
            launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // checks to see if it got any results from MakeProfileFragment
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            assert data != null;
                            Bundle bundle = data.getExtras();
                            // Extracts our Attendee object from the Bundle
                            assert bundle != null;
                            Attendee ourUser = bundle.getParcelable("user");

                            // Switches the layouts, I just turn the startup layout invisible
                            makeProfileLayout = view.findViewById(R.id.startup_profile_layout);
                            makeProfileLayout.setVisibility(View.INVISIBLE);
                            userProfileLayout = view.findViewById(R.id.user_profile_layout);
                            userProfileLayout.setVisibility(View.VISIBLE);

                            assert ourUser != null;
                            editor.putString("user_first_name", ourUser.getFirstName());
                            editor.putString("user_last_name", ourUser.getLastName());
                            editor.putString("user_phone_number", ourUser.getPhoneNumber());
                            editor.putString("user_email", ourUser.getEmail());
                            editor.apply();

                            // sets the text for the Profile layout to the attributes of the attendee
                            firstName.setText(ourUser.getFirstName());
                            lastName.setText(ourUser.getLastName());
                            email.setText(ourUser.getEmail());
                            phoneNumber.setText(ourUser.getPhoneNumber());

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
                            Glide.with(ProfileFragment.this).load(ourUser.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(profilePic);
                        }
                    }
            );
        }

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
            launcher.launch(intent);
        });

        makeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
            launcher.launch(intent);
        });

        return view;
    }
}