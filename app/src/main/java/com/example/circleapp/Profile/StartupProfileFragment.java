package com.example.circleapp.Profile;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.R;

/**
 * A fragment for handling startup profile details.
 */
public class StartupProfileFragment extends Fragment {
    Button makeProfile;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<Intent> launcher;
    static Attendee ourUser;

    /**
     * View prompts the user to make a profile with details. When user chooses to make a profile,
     * MakeProfileActivity is called. The profile details from the activity are packed into a bundle
     * and sent back to the previous fragment.
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
     * @see ProfileFragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        View view = inflater.inflate(R.layout.fragment_startup_profile, container, false);
        makeProfile = view.findViewById(R.id.add_profile_details);

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
                        editor.putString("user_profile_pic", ourUser.getProfilePic());

                        @Nullable String userProfilePic = ourUser.getProfilePic();
                        if (userProfilePic != null) {
                            editor.putString("user_profile_pic", userProfilePic);
                        } else {
                            editor.remove("user_profile_pic");
                        }

                        editor.apply();

                        requireActivity().getSupportFragmentManager().popBackStack();
                    }

                });

        makeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
            intent.putExtra("user", ourUser);
            launcher.launch(intent);
        });

        return view;
    }
}