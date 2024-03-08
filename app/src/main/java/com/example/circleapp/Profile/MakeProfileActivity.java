package com.example.circleapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Activity that starts when User wants to make a profile
public class MakeProfileActivity extends AppCompatActivity {

    Button deleteButton;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    CheckBox geolocationEditText;
    Button confirmButton;
    ImageView profilePic;
    Uri mImageUri;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    DatabaseReference mDatabaseRef;


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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

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
                    Glide.with(this).load(selectedImageUri).apply(RequestOptions.circleCropTransform()).into(profilePic);
                    return null;
                }));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;deleteProfilePicture();
            }
        });




        confirmButton.setOnClickListener(v -> {
            // after clicking confirm, gets all the user inputs from EditTexts etc.
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String ID = firebaseManager.generateRandomUserId();
            boolean isGeoEnabled = geolocationEditText.isChecked();

            // Creates a new user! But need to figure out how we'll be able to edit
            // an existing user because this makes an entirely new Attendee object each time
            Attendee user = new Attendee(ID, firstName, lastName, email, phoneNumber);
            user.setGeoEnabled(isGeoEnabled);

            if(selectedImageUri == null){
                if(((int)firstName.charAt(0)>=65 && (int)firstName.charAt(0)<=90) || ((int)firstName.charAt(0)>=97 && (int)firstName.charAt(0)<=122)){
                    // Construct the resource ID of the drawable
                    int drawableResourceId = getResources().getIdentifier(firstName.charAt(0) + "", "drawable", getPackageName());

                    // Check if the drawable exists
                    if (drawableResourceId != 0) {
                        // Load the drawable into the ImageView using Glide
                        Glide.with(this).load(drawableResourceId).apply(RequestOptions.circleCropTransform()).into(profilePic);
                    }
                }
            }
            user.setProfilePic(selectedImageUri);
            firebaseManager.addNewUser(user);
            PreferenceUtils.setCurrentUserID(this, ID);
            PreferenceUtils.setProfileCreated(this, true);

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
    private void deleteProfilePicture() {
        if (selectedImageUri != null) {
            // Delete the image from storage
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(selectedImageUri.toString());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Deletion successful
                    // Update UI (e.g., set a default image)
                    profilePic.setImageResource(R.drawable.ic_profile_icon);
                    selectedImageUri = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Deletion failed
                    // Handle the failure (e.g., show an error message)
                    Toast.makeText(MakeProfileActivity.this, "Failed to delete profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No image selected
            // Handle accordingly (e.g., show a message)
            Toast.makeText(MakeProfileActivity.this, "No profile picture to delete", Toast.LENGTH_SHORT).show();
        }
    }
}