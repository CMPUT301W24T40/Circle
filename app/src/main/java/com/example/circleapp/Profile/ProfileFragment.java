package com.example.circleapp.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.R;

/**
 * This class is used to display the profile of a user.
 */
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
    static Attendee ourUser;

    /**
     * View prompts the user to make a profile with details. When user chooses to make
     * a profile, MakeProfileActivity is called. The profile details from the activity
     * are displayed here on the view. Choosing to edit the existing profile
     * starts the EditProfileActivity class. Any updates to the details will be
     * displayed on the view.
     *
     * @param inflater          The LayoutInflater object that can be used to inflate
     *                          any views in the fragment,
     * @param container         If non-null, this is the parent view that the fragment's
     *                          UI should be attached to.  The fragment should not add the view itself,
     *                          but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return                  Returns the View shown to the user.
     * @see MakeProfileActivity
     * @see EditProfileActivity
     *
     * Current issues:          Profile picture disappears after navigating away and returning to
     *                          fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

            String pfpString = sharedPreferences.getString("user_profile_pic", null);

            if (pfpString != null) {
                Uri uri = Uri.parse(pfpString);
                Glide.with(ProfileFragment.this).load(uri).apply(RequestOptions.circleCropTransform()).into(profilePic);
            }
            else {
                char firstLetter = firstName.getText().toString().toLowerCase().charAt(0);
                int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", requireContext().getPackageName());
                profilePic.setImageResource(defaultImageResource);
            }
        }

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        ourUser = bundle.getParcelable("user");

                        makeProfileLayout = view.findViewById(R.id.startup_profile_layout);
                        makeProfileLayout.setVisibility(View.INVISIBLE);
                        userProfileLayout = view.findViewById(R.id.user_profile_layout);
                        userProfileLayout.setVisibility(View.VISIBLE);

                        editor.putString("user_first_name", ourUser.getFirstName());
                        editor.putString("user_last_name", ourUser.getLastName());
                        editor.putString("user_phone_number", ourUser.getPhoneNumber());
                        editor.putString("user_email", ourUser.getEmail());
                        editor.apply();

                        firstName.setText(ourUser.getFirstName());
                        lastName.setText(ourUser.getLastName());
                        email.setText(ourUser.getEmail());
                        phoneNumber.setText(ourUser.getPhoneNumber());

                        String checked = "Geolocation: ENABLED";
                        String unchecked = "Geolocation: DISABLED";
                        geolocation.setClickable(false);
                        if (ourUser.isGeoEnabled()) {
                            geolocation.setText(checked);
                            geolocation.setChecked(true);
                        } else {
                            geolocation.setText(unchecked);
                            geolocation.setChecked(false);
                        }

                        if (ourUser.getProfilePic() != null) {
                            Glide.with(ProfileFragment.this).load(ourUser.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(profilePic);
                            editor.putString("user_profile_pic", ourUser.getProfilePic().toString());
                        }
                        else {
                            char firstLetter = ourUser.getFirstName().toLowerCase().charAt(0);
                            int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", requireContext().getPackageName());
                            profilePic.setImageResource(defaultImageResource);
                            editor.putString("user_profile_pic", null);
                        }
                        editor.apply();
                    }
                }
        );

        makeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
            launcher.launch(intent);
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), EditProfileActivity.class);
            intent.putExtra("user", ourUser);
            launcher.launch(intent);
        });

        return view;
    }
}