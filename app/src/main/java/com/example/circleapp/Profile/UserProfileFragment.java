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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.circleapp.Admin.AdminHomeFragment;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.MainActivity;
import com.example.circleapp.R;

import java.util.Objects;

/**
 * This class displays a user's profile information.
 */
public class UserProfileFragment extends Fragment {
    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phoneNumber;
    TextView homepage;
    Button editProfile;
    Button adminView;
    ImageView profilePic;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<Intent> launcher;
    static Attendee ourUser;

    /**
     * This fragment is instantiated from the ProfileFragment class if the user has made a profile.
     * It will display their details, including name, phone number, email, profile picture, and
     * homepage. Users can also edit their profile information by pressing the "Edit Profile" button,
     * and if they have gained admin capabilities, there is a button to switch to the admin interface.
     *
     * @param inflater          The LayoutInflater object that can be used to inflate
     *                          any views in the fragment,
     * @param container         If non-null, this is the parent view that the fragment's
     *                          UI should be attached to.  The fragment should not add the view itself,
     *                          but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return                  Returns the View shown to the user.
     * @see ProfileFragment
     * @see AdminHomeFragment
     * @see MainActivity
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

                        assert ourUser != null;
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

        if (ProfileFragment.isAdmin) {
            adminView = view.findViewById(R.id.admin_view_button);
            adminView.setVisibility(View.VISIBLE);
            adminView.setOnClickListener(v -> {
                ((MainActivity) requireActivity()).setNavBarVisibility(false);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new AdminHomeFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
        }

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
                char firstLetter = Objects.requireNonNull(userFirstName).toLowerCase().charAt(0);
                int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", requireContext().getPackageName());
                profilePic.setImageResource(defaultImageResource);
            }
        }
    }
}