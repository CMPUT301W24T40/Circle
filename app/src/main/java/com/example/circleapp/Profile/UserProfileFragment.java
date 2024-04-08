package com.example.circleapp.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phoneNumber;
    TextView homepage;
    Button makeProfile;
    Button editProfile;
    ImageView profilePic;
    RelativeLayout userProfileLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<Intent> launcher;
    static Attendee ourUser;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    /**
     * View prompts the user to make a profile with details. When user chooses to make a profile,
     * MakeProfileActivity is called. The profile details from the activity are displayed here on
     * the view. Choosing to edit the existing profile starts the EditProfileActivity class. Any
     * updates to the details will be displayed on the view.
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
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);
        homepage = view.findViewById(R.id.homepage);
        editProfile = view.findViewById(R.id.edit_profile_button);
        profilePic = view.findViewById(R.id.edit_pfp);

        firstName.setText(sharedPreferences.getString("user_first_name", null));
        lastName.setText(sharedPreferences.getString("user_last_name", null));
        phoneNumber.setText(sharedPreferences.getString("user_phone_number", null));
        email.setText(sharedPreferences.getString("user_email", null));
        homepage.setText(sharedPreferences.getString("user_homepage", null));
        loadProfileImage();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Bundle bundle = data.getExtras();
                        assert bundle != null;
                        ourUser = bundle.getParcelable("user");

                        editor.putString("user_first_name", ourUser.getFirstName());
                        editor.putString("user_last_name", ourUser.getLastName());
                        editor.putString("user_phone_number", ourUser.getPhoneNumber());
                        editor.putString("user_email", ourUser.getEmail());
                        editor.putString("user_homepage", ourUser.getHomepage());
                        editor.apply();

                        firstName.setText(ourUser.getFirstName());
                        lastName.setText(ourUser.getLastName());
                        email.setText(ourUser.getEmail());
                        phoneNumber.setText(ourUser.getPhoneNumber());
                        homepage.setText(ourUser.getHomepage());

                        Uri userPFP = ourUser.getProfilePic();
                        if (userPFP != null) {
                            Glide.with(UserProfileFragment.this).load(userPFP).into(profilePic);
                            editor.putString("user_profile_pic", userPFP.toString());
                        }
                        else {
                            char firstLetter = ourUser.getFirstName().toLowerCase().charAt(0);
                            int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", requireContext().getPackageName());
                            profilePic.setImageResource(defaultImageResource);
                            editor.putString("user_profile_pic", null);
                        }
                        editor.commit();
                    }
                }
        );

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), EditProfileActivity.class);
            intent.putExtra("user", ourUser);
            launcher.launch(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadProfileImage();
    }

    private void loadProfileImage() {
        if (isAdded() && getActivity() != null) {
            String pfpString = sharedPreferences.getString("user_profile_pic", null);
            if (pfpString != null) {
                Glide.with(UserProfileFragment.this).load(Uri.parse(pfpString)).into(profilePic);
            } else {
                String userFirstName = sharedPreferences.getString("user_first_name", null);
                char firstLetter = userFirstName.toLowerCase().charAt(0);
                int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", requireContext().getPackageName());
                profilePic.setImageResource(defaultImageResource);
            }
        }
    }
}