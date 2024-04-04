package com.example.circleapp.Profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.Firebase.ImageManager;
import com.example.circleapp.R;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * This class is used to edit an already existing user's profile.
 */
public class EditProfileActivity extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText homepageEditText;
    Button confirmButton;
    ImageView profilePic;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    ImageManager imageManager;
    Attendee user;

    /**
     * User can input changes to their profile details upon this Activity being created.
     * Any confirmed changes to their profile will be applied to the user and profile details
     * will be updated accordingly. User's data will be updated on the Firestore database.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see ProfileFragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_make_profile);

        String pfpString = sharedPreferences.getString("user_profile_pic", null);
        user = new Attendee(firebaseManager.getPhoneID(),
                sharedPreferences.getString("user_first_name", null),
                sharedPreferences.getString("user_last_name", null),
                sharedPreferences.getString("user_email", null),
                sharedPreferences.getString("user_phone_number", null),
                sharedPreferences.getString("user_homepage", null),
                (pfpString != null) ? Uri.parse(sharedPreferences.getString("user_profile_pic", null)) : null);

        firstNameEditText = findViewById(R.id.fname_edit);
        lastNameEditText = findViewById(R.id.lname_edit);
        emailEditText = findViewById(R.id.edit_email);
        phoneNumberEditText = findViewById(R.id.edit_number);
        homepageEditText = findViewById(R.id.edit_homepage);
        confirmButton = findViewById(R.id.confirm_edit_button);
        profilePic = findViewById(R.id.edit_pfp);

        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        emailEditText.setText(user.getEmail());
        phoneNumberEditText.setText(user.getPhoneNumber());
        homepageEditText.setText(user.getHomepage());

        if (user.getProfilePic() == null) {
            char firstLetter = user.getFirstName().toLowerCase().charAt(0);
            int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
            profilePic.setImageResource(defaultImageResource);
        }
        else {
            Glide.with(this).load(user.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(profilePic);
        }

        imageManager = new ImageManager(this, profilePic);

        profilePic.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setTitle("Profile Picture Options");
            String[] options = {"Select Image", "Delete Image", "Cancel"};
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        imageManager.selectImage();
                        break;
                    case 1:
                        char firstLetter = user.getFirstName().toLowerCase().charAt(0);
                        int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
                        profilePic.setImageResource(defaultImageResource);
                        user.setprofilePic(null);
                    case 2:
                        break;
                }
            });
            builder.show();
        });

        confirmButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String homepage = homepageEditText.getText().toString();

            user.setfirstName(firstName);
            user.setlastName(lastName);
            user.setphoneNumber(phoneNumber);
            user.setemail(email);
            user.sethomepage(homepage);
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("tag", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("my token", token);
                        user.settoken(token);
                        user.sethasProfile(true);

                        if (imageManager.hasImage()) {
                            imageManager.uploadProfilePictureImage(downloadURL -> {
                                user.setprofilePic(downloadURL);
                                finishActivity(user, downloadURL);
                            });
                        }
                        else {
                            finishActivity(user, null);
                        }
                    });
        });
    }

    /**
     * Handles the result of an activity launched for a result.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The data returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Completes the MakeProfileActivity by adding the user to the database
     * and optionally passing a download URL for a profile picture.
     *
     * @param user        The user to be added to the database.
     * @param downloadURL The download URL of the profile picture (null if no picture was selected).
     */
    private void finishActivity(Attendee user, String downloadURL) {
        firebaseManager.editUser(user, null);

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        if (downloadURL != null) { bundle.putString("userPFP", downloadURL); }
        Intent intent = new Intent();
        intent.putExtras(bundle);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}