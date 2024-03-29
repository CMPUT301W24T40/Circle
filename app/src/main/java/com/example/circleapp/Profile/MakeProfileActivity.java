package com.example.circleapp.Profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.Firebase.ImageManager;
import com.example.circleapp.R;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * This class is used for when a user wants to make profile
 * a.k.a. add their details
 */
public class MakeProfileActivity extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText homepageEditText;
    Button confirmButton;
    ImageView profilePic;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    ImageManager imageManager;

    /**
     * When this Activity is created, a user can add their details to make
     * a profile on the app. Details include name, email, phone number, option for
     * geolocation, and profile pic. After confirmation, user profile is created
     * and added to Firestore database to keep track of the user. The user is put into
     * a Bundle and sent back to the fragment (ProfileFragment) that started the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see ProfileFragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        firstNameEditText = findViewById(R.id.fname_edit);
        lastNameEditText = findViewById(R.id.lname_edit);
        emailEditText = findViewById(R.id.edit_email);
        phoneNumberEditText = findViewById(R.id.edit_number);
        homepageEditText = findViewById(R.id.edit_homepage);
        confirmButton = findViewById(R.id.confirm_edit_button);
        profilePic = findViewById(R.id.edit_pfp);

        imageManager = new ImageManager(this, profilePic);

        profilePic.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MakeProfileActivity.this);
            builder.setTitle("Profile Picture Options");
            String[] options = {"Select Image", "Cancel"};
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        imageManager.selectImage();
                        break;
                    case 1:
                        break;
                }
            });
            builder.show();
        });

        confirmButton.setOnClickListener(v -> {
            // after clicking confirm, gets all the user inputs from EditTexts etc.
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String homepage = homepageEditText.getText().toString();
            String ID = firebaseManager.getPhoneID();

            if (firstName.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakeProfileActivity.this);
                builder.setMessage("Please input at least a first name")
                        .setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }

            Attendee user = new Attendee(ID, firstName, lastName, email, phoneNumber, homepage, null);
            user.setLocationLatitude(null);
            user.setLocationLongitude(null);
            // for notifications, getting the token to send it to particular device
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
                        firebaseManager.addNewUser(user);
                    });

            imageManager.uploadProfilePictureImage(selectedImage -> {
                user.setprofilePic(selectedImage);

                setResult(Activity.RESULT_OK);
                finish();
            });

            // Stuffs the Attendee (user) object into a Bundle and then an Intent to be sent back to the fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            // Letting the fragment know it has results or not in this case it ALWAYS will, for now just to test
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageManager.onActivityResult(requestCode, resultCode, data);
    }
}