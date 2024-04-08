package com.example.circleapp;

import static com.example.circleapp.Profile.MakeProfileActivity.PICK_IMAGE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.Firebase.ImageManager;

import java.util.Objects;

/**
 * This activity allows the user to input temporary user information.
 */
public class TempUserInfoActivity extends AppCompatActivity {
    EditText firstNameEditText;
    Button confirmButton;
    ImageView profilePic;
    Uri selectedImageUri;
    final double NULL_DOUBLE = -999999999;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    ImageManager imageManager;
    Attendee user;

    /**
     * When this Activity is created,
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempuserinfo);

        firstNameEditText = findViewById(R.id.fname_edit);
        confirmButton = findViewById(R.id.confirm_edit_button);
        profilePic = findViewById(R.id.edit_pfp);

        imageManager = new ImageManager(this, profilePic);

        profilePic.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TempUserInfoActivity.this);
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
            String firstName = firstNameEditText.getText().toString();
            String ID = firebaseManager.getPhoneID();

            if (firstName.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TempUserInfoActivity.this);
                builder.setMessage("Please input at least a first name")
                        .setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }

            user = new Attendee(ID, firstName, null, null, null, null, selectedImageUri);
            user.setHasProfile(false);
            user.setLocationLatitude(NULL_DOUBLE);
            user.setLocationLongitude(NULL_DOUBLE);

            if (selectedImageUri != null) {
                imageManager.uploadProfilePictureImage(selectedImageUri);
            }

            firebaseManager.addNewUser(user);

            finish();
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
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            this.selectedImageUri = imageManager.onActivityResult(requestCode, resultCode, data);
            if (user != null) {
                user.setprofilePic(String.valueOf(selectedImageUri));
            }
        }
    }
}