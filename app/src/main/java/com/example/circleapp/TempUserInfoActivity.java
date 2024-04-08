package com.example.circleapp;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;

import java.util.Objects;

/**
 * This activity allows the user to input temporary user information.
 */
public class TempUserInfoActivity extends AppCompatActivity {
    EditText firstNameEditText;
    Button confirmButton;
    ImageView profilePic;
    Uri selectedImageUri;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

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

        selectedImageUri = null;
        profilePic.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TempUserInfoActivity.this);
            builder.setTitle("Profile Picture Options");
            String[] options = {"Select Image", "Cancel"};
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        selectImage();
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

            Attendee user = new Attendee(ID, firstName, null, null, null, null, selectedImageUri);
            user.setHasProfile(false);

            firebaseManager.addNewUser(user);

            finish();
        });

    }

    /**
     * Method to launch an intent to select an image from the device's gallery.
     */
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickResultLauncher.launch(intent);
    }

    /**
     * Activity result launcher for handling image selection result.
     */
    ActivityResultLauncher<Intent> imagePickResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && Objects.requireNonNull(result.getData()).getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                } else {
                    Toast.makeText(
                            TempUserInfoActivity.this,
                            "Image Not Selected",
                            Toast.LENGTH_SHORT).show();
                }
            });
}