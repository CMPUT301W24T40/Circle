package com.example.circleapp.EventDisplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.circleapp.BaseObjects.Announcement;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.Firebase.FirebaseManager;
import com.example.circleapp.QRCode.GenerateQRActivity;
import com.example.circleapp.QRCode.ReuseQRActivity;
import com.example.circleapp.R;
import com.example.circleapp.TempUserInfoActivity;
import com.example.circleapp.UserDisplay.GuestlistActivity;
import com.example.circleapp.SendNotificationActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This class is used to display event details.
 */
public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance(); // FirebaseManager instance
    Button backButton;
    Button generateQRButton;
    Button reuseQRButton;
    Button registerButton;
    Button guestlistButton;
    Button addAnnouncementButton;
    TextView eventNameTextView;
    TextView eventLocationTextView;
    TextView eventDateTextView;
    TextView eventTimeTextView;
    TextView eventDescriptionTextView;
    TextView eventCapacityTextView;
    ImageView eventPosterImageView;
    private ArrayList<Announcement> announcementsList;
    private AnnouncementAdapter announcementAdapter;
    private ListView listView;

    /**
     * When this Activity is created, a user can view the details of the event they clicked on
     * (clicked on from either BrowseEventsFragment or YourEventsFragment). Within this page, there
     * will be a button to generate a QR code that the user can send to other users to share the event
     * details. If the user clicked on the event from the BrowseEventsFragment, there will also be a
     * button to register for the event. After confirmation of registration, this event will be added
     * to the user's registeredEvents sub-collection in Firestore (sub-collection of user document), and
     * will be displayed on the YourEventsFragment. There is also a back button that will send the user
     * back to the fragment this activity was launched from.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle)
     * @see BrowseEventsFragment
     * @see YourEventsFragment
     * @see GenerateQRActivity
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        event = getIntent().getParcelableExtra("event");
        assert event != null;
        String eventId = event.getID(); // Use getID() to access the event's ID

        TextView noAnnouncementsTextView = findViewById(R.id.no_announcements_textview);
        listView = findViewById(R.id.announcement_listview);

        // Initialize announcements list
        announcementsList = new ArrayList<>();
        // Load announcements from Firebase and then set up the ListView and its adapter
        firebaseManager.loadAnnouncements(eventId, announcements -> {
            announcementsList.clear();
            announcementsList.addAll(announcements);

            announcementAdapter = new AnnouncementAdapter(EventDetailsActivity.this, announcementsList);
            listView.setAdapter(announcementAdapter);

            // After loading announcements, check if the list is empty
            if (announcementsList.isEmpty()) {
                noAnnouncementsTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noAnnouncementsTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });
        //updates list immediately after adding announcement (from organizer's view)
        listView.setAdapter(announcementAdapter);

        // Initialize views
        eventNameTextView = findViewById(R.id.event_details_name);
        eventLocationTextView = findViewById(R.id.event_details_location);
        eventDateTextView = findViewById(R.id.event_details_date);
        eventTimeTextView = findViewById(R.id.event_details_time);
        eventDescriptionTextView = findViewById(R.id.event_details_description);
        eventCapacityTextView = findViewById(R.id.event_details_capacity);
        eventPosterImageView = findViewById(R.id.event_details_poster);

        // Set event details
        eventNameTextView.setText(event.getEventName());
        eventLocationTextView.setText(event.getLocation());
        eventDateTextView.setText(event.getDate());
        eventTimeTextView.setText(event.getTime());
        eventDescriptionTextView.setText(event.getDescription());

        if (event.getCapacity().equalsIgnoreCase("-1")) {
            eventCapacityTextView.setText("Not specified");
        }
        else {
            eventCapacityTextView.setText(event.getCapacity());
        }


        // Load event poster image
        String eventPosterURL = event.getEventPosterURL();
        if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
            Glide.with(this)
                    .load(eventPosterURL)
                    .apply(new RequestOptions().placeholder(R.drawable.no_poster))
                    .into(eventPosterImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.no_poster)
                    .into(eventPosterImageView);
        }

        // Initialize buttons
        backButton = findViewById(R.id.back_button);
        generateQRButton = findViewById(R.id.generate_qr_button);
        registerButton = findViewById(R.id.register_button);
        guestlistButton = findViewById(R.id.guestlist_button);
        reuseQRButton = findViewById(R.id.reuse_qr_button);
        addAnnouncementButton = findViewById(R.id.add_announcement_button);
        addAnnouncementButton.setOnClickListener(v -> showAddAnnouncementDialog());

        // Set visibility of register, guest list, and QR buttons based on source
        String source = getIntent().getStringExtra("source");

        registerButton.setVisibility(View.GONE);
        if ("BrowseEventsFragment".equals(source)) {
            registerButton.setVisibility(View.VISIBLE);
        }

        guestlistButton.setVisibility(View.GONE);
        generateQRButton.setVisibility(View.GONE);
        reuseQRButton.setVisibility(View.GONE);
        addAnnouncementButton.setVisibility(View.GONE);
        if ("CreatedEventsFragment".equals(source)) {
            guestlistButton.setVisibility(View.VISIBLE);
            generateQRButton.setVisibility(View.VISIBLE);
            reuseQRButton.setVisibility(View.VISIBLE);
            addAnnouncementButton.setVisibility(View.VISIBLE);
        }

        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Generate QR details button click listener
        generateQRButton.setOnClickListener(v -> {
            // Create an AlertDialog to ask the user what type of QR code they want to generate
            //TODO: Customize alertdialog box
            new AlertDialog.Builder(this)
                    .setTitle("QR Code Type")
                    .setMessage("What type of QR code would you like to generate?")
                    .setPositiveButton("Check-in", (dialog, which) -> startGenerateQRActivity("check-in"))
                    .setNegativeButton("Details", (dialog, which) -> startGenerateQRActivity("details"))
                    .show();
        });

        // Reuse QR button click listener
        reuseQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReuseQRActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        // Register button click listener
        registerButton.setOnClickListener(v -> firebaseManager.checkUserExists(exists -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
            if (exists) {
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to register?");
                builder.setPositiveButton("Yes", (dialog, which) -> firebaseManager.registerEvent(event, this));
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                builder.setTitle("Details needed");
                builder.setMessage("Before creating an event, we need some details from you");
                builder.setPositiveButton("Let's go!", (dialog, which) -> {
                    Intent intent = new Intent(this, TempUserInfoActivity.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }));

        guestlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestlistActivity.class);
            intent.putExtra("source", "GuestlistActivity");
            intent.putExtra("event", event);
            startActivity(intent);
        });

        // To allow organizers to modify announcements
        setupAnnouncementInteractionRole();

    }
    private void setupAnnouncementInteractionRole() {
        String source = getIntent().getStringExtra("source");

        if ("CreatedEventsFragment".equals(source)) {
            // The user is an organizer if they came from CreatedEventsFragment
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Announcement selectedAnnouncement = announcementsList.get(position);
                showOptionsDialog(selectedAnnouncement);
            });
        }
    }
    private void showOptionsDialog(Announcement announcement) {
        new AlertDialog.Builder(this)
                .setTitle("Announcement Options")
                .setItems(new CharSequence[]{"Edit","Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        editAnnouncement(announcement);
                    } else if (which == 1) {
                        deleteAnnouncement(announcement);
                    }
                })
                .show();
    }

    private void editAnnouncement(Announcement announcement) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_announcement);
        dialog.setCancelable(true);

        EditText announcementEditText = dialog.findViewById(R.id.edit_text_announcement);
        Button editButton = dialog.findViewById(R.id.button_add_announcement);
        announcementEditText.setText(announcement.getContent());

        editButton.setOnClickListener(v -> {
            String updatedText = announcementEditText.getText().toString().trim();
            if (!updatedText.isEmpty()) {
                announcement.setContent(updatedText);
                announcement.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date())); // Update timestamp

                firebaseManager.updateAnnouncement(event.getID(), announcement, task -> {
                    if (task.isSuccessful()) {

                        announcementAdapter.notifyDataSetChanged();
                        Toast.makeText(EventDetailsActivity.this, "Announcement updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventDetailsActivity.this, "Failed to update announcement", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
            } else {
                Toast.makeText(EventDetailsActivity.this, "Please enter an announcement", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void deleteAnnouncement(Announcement announcement) {
        firebaseManager.deleteAnnouncement(event.getID(), announcement.getAnnouncementID(), () -> {
            // onSuccess
            announcementsList.remove(announcement);
            announcementAdapter.notifyDataSetChanged();
            Toast.makeText(EventDetailsActivity.this, "Announcement deleted successfully", Toast.LENGTH_SHORT).show();
        }, errorMessage -> {
            // onError
            Toast.makeText(EventDetailsActivity.this, "Failed to delete announcement: " + errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
    private void showAddAnnouncementDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_announcement);
        dialog.setCancelable(true);

        EditText announcementEditText = dialog.findViewById(R.id.edit_text_announcement);
        Button addButton = dialog.findViewById(R.id.button_add_announcement);
        // for notifications to see who to send the notif too
        ArrayList<Attendee> attendees = firebaseManager.getRegisteredUserTokens(event.getID());


        addButton.setOnClickListener(v -> {
            String announcementText = announcementEditText.getText().toString().trim();
            if (!announcementText.isEmpty()) {
                Announcement announcement = new Announcement();
                announcement.setContent(announcementText);
                announcement.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
                announcement.setAnnouncementID(firebaseManager.generateRandomID());

                // Use the event's ID to add the announcement to the correct event in Firebase
                firebaseManager.addAnnouncement(event.getID(), announcement, task -> {
                    if (task.isSuccessful()) {
                        announcementsList.add(announcement);
                        announcementAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EventDetailsActivity.this, "Failed to add announcement", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
            } else {
                Toast.makeText(EventDetailsActivity.this, "Please enter an announcement", Toast.LENGTH_SHORT).show();
            }
            for (Attendee attendee : attendees) {
                assert attendee != null;
                String token = attendee.getToken();
                SendNotificationActivity.sendAnnouncementNotif(token, event.getEventName());
            }
        });

        Button backButton = dialog.findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    // New method to start GenerateQRActivity with the given qrType
    private void startGenerateQRActivity(String qrType) {
        Intent intent = new Intent(this, GenerateQRActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("qrType", qrType);
        startActivity(intent);
    }
}