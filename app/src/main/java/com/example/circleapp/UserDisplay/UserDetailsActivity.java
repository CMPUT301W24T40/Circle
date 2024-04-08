package com.example.circleapp.UserDisplay;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
        profilePic = findViewById(R.id.edit_pfp);

        firebaseManager.checkProfileExists(user.getID(), exists -> {
            if (exists) {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                phoneNumber.setText(user.getPhoneNumber());
                email.setText(user.getEmail());
                homepage.setText(user.getHomepage());

                if (user.getProfilePic() != null) {
                    if (!user.getProfilePic().toString().equals("null")) {
                        Glide.with(this).load(user.getProfilePic()).into(profilePic);
                    } else {
                        char firstLetter = user.getFirstName().toLowerCase().charAt(0);
                        int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
                        profilePic.setImageResource(defaultImageResource);
                    }
                }
                else {
                    char firstLetter = user.getFirstName().toLowerCase().charAt(0);
                    int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
                    profilePic.setImageResource(defaultImageResource);
                }
            }
            else {
                firstName.setText(user.getFirstName());
                lastName.setText("Unavailable");
                phoneNumber.setText("Unavailable");
                email.setText("Unavailable");
                homepage.setText("Unavailable");

                if (user.getProfilePic() != null) { Glide.with(this).load(user.getProfilePic()).into(profilePic); }
                else {
                    char firstLetter = user.getFirstName().toLowerCase().charAt(0);
                    int defaultImageResource = getResources().getIdentifier(firstLetter + "", "drawable", this.getPackageName());
                    profilePic.setImageResource(defaultImageResource);
                }
            }
        });
    }
}
