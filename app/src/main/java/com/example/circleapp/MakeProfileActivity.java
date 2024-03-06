package com.example.circleapp;

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
import com.github.dhaval2404.imagepicker.ImagePicker;

// Activity that starts when User wants to make a profile
public class MakeProfileActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);


        firstNameEditText = findViewById(R.id.fname_edit);
        lastNameEditText = findViewById(R.id.lname_edit);
        emailEditText = findViewById(R.id.edit_email);
        phoneNumberEditText = findViewById(R.id.edit_number);
        geolocationEditText = findViewById(R.id.edit_geolocation);
        confirmButton = findViewById(R.id.confirm_edit_button);
        profilePic = findViewById(R.id.edit_pfp);

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
        profilePic.setOnClickListener(v -> ImagePicker.with(MakeProfileActivity.this).cropSquare().compress(512).maxResultSize(512,512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        confirmButton.setOnClickListener(v -> {
            // after clicking confirm, gets all the user inputs from EditTexts etc.
            String firstName = firstNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String ID = firebaseManager.generateRandomUserId();
            boolean isGeoEnabled = geolocationEditText.isChecked();

            // Creates a new user! But need to figure out how we'll be able to edit
            // an existing user because this makes an entirely new Attendee object each time
            Attendee user = new Attendee(ID, firstName, email);
            user.setGeoEnabled(isGeoEnabled);
            user.setProfilePic(selectedImageUri);

            firebaseManager.addNewUser(user);
            //PreferenceUtils.setCurrentUserID(this, ID);
            //PreferenceUtils.setProfileCreated(this, true);

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