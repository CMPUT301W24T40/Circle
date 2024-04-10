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
    @Nullable
    Uri selectedImageUri;
    final double NULL_DOUBLE = -999999999;
    private static final int IMAGE_PICK = 1;

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

        user = new Attendee(firebaseManager.getPhoneID(),
                sharedPreferences.getString("user_first_name", null),
                sharedPreferences.getString("user_last_name", null),
                sharedPreferences.getString("user_email", null),
                sharedPreferences.getString("user_phone_number", null),
                sharedPreferences.getString("user_homepage", null),
                sharedPreferences.getString("user_profile_pic", null));

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
            selectedImageUri = null;
        }
        else {
            Glide.with(this).load(user.getProfilePic()).into(profilePic);
            selectedImageUri = Uri.parse(user.getProfilePic());
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
                        selectedImageUri = null;
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
            user.setLocationLatitude(NULL_DOUBLE);
            user.setLocationLongitude(NULL_DOUBLE);
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("tag", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("my token", token);
                        user.settoken(token);
                    });

            user.setHasProfile(true);
            ProfileFragment.ProfileMade = true;

//            if (selectedImageUri == null) {
//                imageManager.uploadProfilePictureImage(null, new ImageManager.OnImageUploadListener() {
//                    @Override
//                    public void onUploadSuccess(String downloadUrl) {
//                        // empty since this method wouldn't be used if imageUri is null
//                    }
//
//                    @Override
//                    public void onUploadFailure() {
//                        firebaseManager.editUser(user, null);
//
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("user", user);
//                        Intent intent = new Intent();
//                        intent.putExtras(bundle);
//
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
//                    }
//                });
            if (selectedImageUri != Uri.parse(String.valueOf(user.getProfilePic()))) {
                imageManager.uploadProfilePictureImage(selectedImageUri, new ImageManager.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String downloadUrl) {
                        user.setprofilePic(downloadUrl);
                        firebaseManager.editUser(user, null);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", user);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onUploadFailure() {
                        firebaseManager.editUser(user, null);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", user);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            } else {
                firebaseManager.editUser(user, null);

                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
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
        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = imageManager.onActivityResult(requestCode, resultCode, data);
        }
     }
}