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

/**
 * This class is used for when a user wants to make profile
 * a.k.a. add their details
 */
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

    /**
     * When this Activity is created, a user can add their details to make
     * a profile on the app. Details include name, email, phone number, option for
     * geolocation, and profile pic. After confirmation, user profile is created
     * and added to Firestore database to keep track of the user. The user is put into
     * a Bundle and sent back to the fragment (ProfileFragment) that started the activity.
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
            String lastName = lastNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String ID = firebaseManager.generateRandomID();
            boolean isGeoEnabled = geolocationEditText.isChecked();

            // Creates a new user! But need to figure out how we'll be able to edit
            // an existing user because this makes an entirely new Attendee object each time
            Attendee user = new Attendee(ID, firstName, lastName, email, phoneNumber, selectedImageUri);
            user.setGeoEnabled(isGeoEnabled);
            user.setProfilePic(selectedImageUri);

            firebaseManager.addNewUser(user);
            firebaseManager.setCurrentUserID(ID);

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