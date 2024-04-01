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
 * This class is used to display details of events the user has registered for or created.
 */
public class EventDetailsActivity extends AppCompatActivity {
    Event event;
    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    Button generateQRButton;
    Button reuseQRButton;
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

    private TextView currentAttendeesTextView;

    /**
     * When this Activity is created, a user can view the details of the event they clicked on
     * (clicked on from either CreatedEventsFragment or RegisteredEventsFragment). Within this page, there
     * will be a button to generate a QR code that the user can send to other users to share the event
     * details. There will also be a list of announcements for the event and a back button that will
     * send the user back to the fragment this activity was launched from.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle)
     * @see CreatedEventsFragment
     * @see RegisteredEventsFragment
     * @see GenerateQRActivity
     * @see AnnouncementAdapter
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        event = getIntent().getParcelableExtra("event");
        assert event != null;
        String eventId = event.getID();

        currentAttendeesTextView = findViewById(R.id.current_attendees_textview);

        // Determine if the current user is an organizer (For real time attendance)
        String source = getIntent().getStringExtra("source");
        if ("CreatedEventsFragment".equals(source)) {
            // start tracking and showing attendee count
            FirebaseManager.getInstance().trackCheckIns(eventId, this::updateAttendeeCount);
        } else {
            currentAttendeesTextView.setVisibility(View.GONE);
        }


        TextView noAnnouncementsTextView = findViewById(R.id.no_announcements_textview);
        listView = findViewById(R.id.announcement_listview);

        announcementsList = new ArrayList<>();
        firebaseManager.loadAnnouncements(eventId, announcements -> {
            announcementsList.clear();
            announcementsList.addAll(announcements);

            announcementAdapter = new AnnouncementAdapter(EventDetailsActivity.this, announcementsList);
            listView.setAdapter(announcementAdapter);

            if (announcementsList.isEmpty()) {
                noAnnouncementsTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noAnnouncementsTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

        listView.setAdapter(announcementAdapter);

        eventNameTextView = findViewById(R.id.event_details_name);
        eventLocationTextView = findViewById(R.id.event_details_location);
        eventDateTextView = findViewById(R.id.event_details_date);
        eventTimeTextView = findViewById(R.id.event_details_time);
        eventDescriptionTextView = findViewById(R.id.event_details_description);
        eventCapacityTextView = findViewById(R.id.event_details_capacity);
        eventPosterImageView = findViewById(R.id.event_details_poster);

        eventNameTextView.setText(event.getEventName());
        eventLocationTextView.setText(event.getLocation());
        eventDateTextView.setText(event.getDate());
        eventTimeTextView.setText(event.getTime());
        eventDescriptionTextView.setText(event.getDescription());

        if (event.getCapacity().equalsIgnoreCase("-1")) {
            eventCapacityTextView.setText("Not specified");
        } else {
            eventCapacityTextView.setText(event.getCapacity());
        }

        String eventPosterURL = event.getEventPosterURL();
        if (eventPosterURL != null && !eventPosterURL.isEmpty()) {
            Glide.with(this).load(eventPosterURL).apply(new RequestOptions().placeholder(R.drawable.no_poster)).into(eventPosterImageView);
        } else {
            Glide.with(this).load(R.drawable.no_poster).into(eventPosterImageView);
        }

        generateQRButton = findViewById(R.id.generate_qr_button);
        guestlistButton = findViewById(R.id.guestlist_button);
        reuseQRButton = findViewById(R.id.reuse_qr_button);
        addAnnouncementButton = findViewById(R.id.add_announcement_button);
        addAnnouncementButton.setOnClickListener(v -> showAddAnnouncementDialog());

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


        generateQRButton.setOnClickListener(v -> {
            //TODO: Customize alertdialog box
            new AlertDialog.Builder(this)
                    .setTitle("QR Code Type")
                    .setMessage("What type of QR code would you like to generate?")
                    .setPositiveButton("Check-in", (dialog, which) -> startGenerateQRActivity("check-in"))
                    .setNegativeButton("Details", (dialog, which) -> startGenerateQRActivity("details"))
                    .show();
        });

        reuseQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReuseQRActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        guestlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestlistActivity.class);
            intent.putExtra("source", "GuestlistActivity");
            intent.putExtra("event", event);
            startActivity(intent);
        });

        setupAnnouncementInteractionRole();
    }

    /**
     * Sets up interaction for announcements in the event details.
     * Depending on the source, sets a click listener to show options for a selected announcement.
     */
    private void setupAnnouncementInteractionRole() {
        String source = getIntent().getStringExtra("source");

        if ("CreatedEventsFragment".equals(source)) {
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Announcement selectedAnnouncement = announcementsList.get(position);
                showOptionsDialog(selectedAnnouncement);
            });
        }
    }

    /**
     * Shows a dialog with options for the given announcement.
     * Options include editing and deleting the announcement.
     *
     * @param announcement The announcement to show options for.
     */
    private void showOptionsDialog(Announcement announcement) {
        new AlertDialog.Builder(this)
                .setTitle("Announcement Options")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        editAnnouncement(announcement);
                    } else if (which == 1) {
                        deleteAnnouncement(announcement);
                    }
                })
                .show();
    }

    /**
     * Displays a dialog to edit the content of the announcement.
     * Updates the announcement on Firebase upon confirmation.
     *
     * @param announcement The announcement to be edited.
     */
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

    /**
     * Deletes the specified announcement from Firebase.
     * Updates the UI accordingly upon successful deletion.
     *
     * @param announcement The announcement to be deleted.
     */
    private void deleteAnnouncement(Announcement announcement) {
        firebaseManager.deleteAnnouncement(event.getID(), announcement.getAnnouncementID(), () -> {
            announcementsList.remove(announcement);
            announcementAdapter.notifyDataSetChanged();
            Toast.makeText(EventDetailsActivity.this, "Announcement deleted successfully", Toast.LENGTH_SHORT).show();
        }, errorMessage -> Toast.makeText(EventDetailsActivity.this, "Failed to delete announcement: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays a dialog to add a new announcement.
     * Adds the announcement to Firebase upon confirmation.
     * Sends notification to all attendees upon successful addition.
     */
    private void showAddAnnouncementDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_announcement);
        dialog.setCancelable(true);

        EditText announcementEditText = dialog.findViewById(R.id.edit_text_announcement);
        Button addButton = dialog.findViewById(R.id.button_add_announcement);

        ArrayList<Attendee> attendees = firebaseManager.getRegisteredUserTokens(event.getID());

        addButton.setOnClickListener(v -> {
            String announcementText = announcementEditText.getText().toString().trim();
            if (!announcementText.isEmpty()) {
                Announcement announcement = new Announcement();
                announcement.setContent(announcementText);
                announcement.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
                announcement.setAnnouncementID(firebaseManager.generateRandomID());

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

    /**
     * Starts the activity to generate QR code for the event with the specified type.
     *
     * @param qrType The type of QR code to generate.
     */
    private void startGenerateQRActivity(String qrType) {
        Intent intent = new Intent(this, GenerateQRActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("qrType", qrType);
        startActivity(intent);
    }

    //For real-time attendance
    private void updateAttendeeCount(Integer count) {
        runOnUiThread(() -> currentAttendeesTextView.setText(count.toString()));
    }
}