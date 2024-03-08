package com.example.circleapp.Profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.FirebaseManager;
import com.example.circleapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    CheckBox geolocationEditText;
    Button confirmButton;
    ImageView profilePic;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Attendee user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);
        user = Objects.requireNonNull(getIntent().getExtras()).getParcelable("user");

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

        // initializes image pick launcher and loads image
        imagePickLauncher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data!=null && data.getData()!=null) {
                    selectedImageUri = data.getData();
                    Glide.with(this).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                }
            }
        }
        );

        // let's user select an image
        profilePic.setOnClickListener(v -> ImagePicker.with(EditProfileActivity.this).cropSquare().compress(512).maxResultSize(512,512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        confirmButton.setOnClickListener(v -> {
            // after clicking confirm, gets all the user inputs from EditTexts etc.
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String email = emailEditText.getText().toString();
            boolean isGeoEnabled = geolocationEditText.isChecked();

            // Creates a new user! But need to figure out how we'll be able to edit
            // an existing user because this makes an entirely new Attendee object each time
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            user.setGeoEnabled(isGeoEnabled);
            user.setProfilePic(selectedImageUri);

            firebaseManager.editUser(user);

            // Stuffs the Attendee (user) object into a Bundle and then an Intent to be
            // sent back to the fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            // Letting the fragment know it has results or not
            // in this case it ALWAYS will, for now just to test
            setResult(Activity.RESULT_OK, intent);

            // Closes the activity
            ProfileFragment.profileMade = true;
            finish();
        });

    }
}
