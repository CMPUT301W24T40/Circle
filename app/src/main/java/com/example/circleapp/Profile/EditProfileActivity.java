package com.example.circleapp.Profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;

/**
 * This class is used to edit an already existing user's profile.
 */

public class EditProfileActivity extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    CardView circleBackground;
    CheckBox geolocationEditText;
    Button confirmButton;
    ImageView profilePic;
    Uri selectedImageUri;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Attendee user;

    /**
     * User can input changes to their profile details upon this Activity being created.
     * Any confirmed changes to their profile will be applied to the user and profile details
     * will be updated accordingly. User's data will be updated on the Firestore database.
     * Updated user will be sent back to the fragment (ProfileFragment)
     * that called the activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see ProfileFragment
     *
     * Current issues: Custom profile picture does not show up in editing screen; still allows selection
     * of profile picture, but when Activity is initially created, profilePicture imageView is set to a
     * predefined default drawable resource
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        user = getIntent().getParcelableExtra("user");

        firstNameEditText = findViewById(R.id.fname_edit);
        lastNameEditText = findViewById(R.id.lname_edit);
        emailEditText = findViewById(R.id.edit_email);
        phoneNumberEditText = findViewById(R.id.edit_number);
        geolocationEditText = findViewById(R.id.edit_geolocation);
        confirmButton = findViewById(R.id.confirm_edit_button);
        profilePic = findViewById(R.id.edit_pfp);

        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        emailEditText.setText(user.getEmail());
        phoneNumberEditText.setText(user.getPhoneNumber());

        geolocationEditText.setChecked(user.isGeoEnabled());

        if (user.getProfilePic() == null) {
            char firstLetter = user.getFirstName().toLowerCase().charAt(0);
            int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
            profilePic.setImageResource(defaultImageResource);
        }
        else {
            Glide.with(this).load(user.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(profilePic);
        }

        profilePic.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setTitle("Profile Picture Options");
            String[] options = {"Select Image", "Delete Image", "Cancel"};
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        selectImage();
                        break;
                    case 1:
                        selectedImageUri = null;
                        char firstLetter = user.getFirstName().toLowerCase().charAt(0);
                        int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
                        profilePic.setImageResource(defaultImageResource);
                        user.setProfilePic(null);
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
            boolean isGeoEnabled = geolocationEditText.isChecked();

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            user.setGeoEnabled(isGeoEnabled);

            firebaseManager.editUser(user);

            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            setResult(Activity.RESULT_OK, intent);
            ProfileFragment.profileMade = true;
            finish();
        });
    }

    /**
     * Displays an AlertDialog to allow the user to select a profile picture.
     * The AlertDialog contains a GridView displaying a grid of selectable images.
     *
     *
     */
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> imagePickResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    user.setProfilePic(selectedImageUri);
                    Glide.with(this).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                } else {
                    Toast.makeText(
                            EditProfileActivity.this,
                            "Image Not Selected",
                            Toast.LENGTH_SHORT).show();
                }
            });
}