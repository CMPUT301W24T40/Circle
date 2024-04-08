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
 * Use the {@link StartupProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartupProfileFragment extends Fragment {
    Button makeProfile;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<Intent> launcher;
    static Attendee ourUser;

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

                        editor.putString("user_first_name", ourUser.getFirstName());
                        editor.putString("user_last_name", ourUser.getLastName());
                        editor.putString("user_phone_number", ourUser.getPhoneNumber());
                        editor.putString("user_email", ourUser.getEmail());
                        editor.putString("user_homepage", ourUser.getHomepage());
                        editor.apply();

                        Uri userPFP = ourUser.getProfilePic();
                        if (userPFP != null) {
                            editor.putString("user_profile_pic", userPFP.toString());
                        }
                        else {
                            editor.putString("user_profile_pic", null);
                        }
                        editor.commit();

                        navigateBackToProfileFragment();
                    }
                }
        );

        makeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), MakeProfileActivity.class);
            intent.putExtra("user", ourUser);
            launcher.launch(intent);
        });

        return view;
    }

    // Method to navigate back to ProfileFragment
    private void navigateBackToProfileFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}