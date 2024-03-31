package com.example.circleapp.UserDisplay;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.R;

/**
 * This class is used to display details of a user.
 */
public class UserDetailsActivity extends AppCompatActivity {
    Attendee user;
    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phoneNumber;
    TextView homepage;
    CheckBox geolocation;
    Button backButton;
    ImageView profilePic;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    /**
     *
     * When this Activity is created, a user can view the details of the user they clicked on. Within
     * this page, there is a back button that will send the user back to the previous page.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle)
     *
     * @see GuestlistActivity
     * @see com.example.circleapp.Admin.AdminBrowseUsersFragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        user = getIntent().getParcelableExtra("attendee");

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        homepage = findViewById(R.id.homepage);
        geolocation = findViewById(R.id.edit_geolocation);
        profilePic = findViewById(R.id.edit_pfp);
        backButton = findViewById(R.id.back_button);

        Log.d("Has profile", " = " + ((user.hasProfile()) ? "true" : "false"));

        firebaseManager.checkProfileExists(user.getID(), exists -> {
            if (exists) {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                phoneNumber.setText(user.getPhoneNumber());
                email.setText(user.getEmail());
                homepage.setText(user.getHomepage());

                String pfpString = user.getProfilePic().toString();

                if (user.getProfilePic() != null) {
                    Uri uri = Uri.parse(pfpString);
                    profilePic.setImageURI(uri);
                }
                else {
                    char firstLetter = user.getFirstName().toLowerCase().charAt(0);
                    int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
                    profilePic.setImageResource(defaultImageResource);
                }
            }
        });

        backButton.setOnClickListener(v -> finish());
    }
}
