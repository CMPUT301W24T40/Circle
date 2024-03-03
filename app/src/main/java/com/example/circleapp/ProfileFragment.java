package com.example.circleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phoneNumber;
    CheckBox geolocation;
    Button scanButton;
    Button makeProfile;
    Button editProfile;
    RelativeLayout makeProfileLayout;
    RelativeLayout userProfileLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle users = this.getArguments();

        if (users != null) {
            Attendee ourUser = users.getParcelable("user");

            firstName = view.findViewById(R.id.first_name);
            email = view.findViewById(R.id.email);

            if (ourUser != null) {
                makeProfileLayout = view.findViewById(R.id.startup_profile_layout);
                makeProfileLayout.setVisibility(View.INVISIBLE);
                userProfileLayout = view.findViewById(R.id.user_profile_layout);
                userProfileLayout.setVisibility(View.VISIBLE);
                firstName.setText(ourUser.getName());
                email.setText(ourUser.getEmail());

                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
                        startActivity(intent);
                    }
                });
        }
            return view;
        }
        scanButton = view.findViewById(R.id.scan_button);
        makeProfile = view.findViewById(R.id.add_profile_details);

        makeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScanQRActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
